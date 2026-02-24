package com.alkhufash.music.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مدير تحويل الصوت إلى نص باستخدام Android SpeechRecognizer المدمج
 * يدعم اللغتين العربية والإنجليزية
 */
@Singleton
class SpeechToTextManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SpeechToTextManager"
    }

    private var speechRecognizer: SpeechRecognizer? = null

    // حالة التسجيل
    sealed class RecognitionState {
        object Idle : RecognitionState()
        object Listening : RecognitionState()
        data class PartialResult(val text: String) : RecognitionState()
        data class FinalResult(val text: String) : RecognitionState()
        data class Error(val message: String, val errorCode: Int = -1) : RecognitionState()
    }

    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    val recognitionState: StateFlow<RecognitionState> = _recognitionState.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    // قائمة النصوص المحولة المحفوظة
    private val _transcriptions = MutableStateFlow<List<TranscriptionRecord>>(emptyList())
    val transcriptions: StateFlow<List<TranscriptionRecord>> = _transcriptions.asStateFlow()

    /**
     * التحقق من توفر خدمة التعرف على الكلام
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * بدء الاستماع وتحويل الصوت إلى نص
     * @param language اللغة المطلوبة (ar-SA للعربية، en-US للإنجليزية)
     * @param continuous هل الاستماع مستمر أم لمرة واحدة
     */
    fun startListening(language: String = "ar-SA", continuous: Boolean = false) {
        if (_isListening.value) {
            Log.w(TAG, "المستمع يعمل بالفعل")
            return
        }

        if (!isAvailable()) {
            _recognitionState.value = RecognitionState.Error("خدمة التعرف على الكلام غير متوفرة على هذا الجهاز")
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                if (continuous) {
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
                }
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
            _recognitionState.value = RecognitionState.Listening
            Log.d(TAG, "بدء الاستماع باللغة: $language")

        } catch (e: Exception) {
            Log.e(TAG, "خطأ في بدء الاستماع: ${e.message}")
            _recognitionState.value = RecognitionState.Error("فشل بدء الاستماع: ${e.message}")
            _isListening.value = false
        }
    }

    /**
     * إيقاف الاستماع
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _isListening.value = false
            Log.d(TAG, "تم إيقاف الاستماع")
        } catch (e: Exception) {
            Log.e(TAG, "خطأ في إيقاف الاستماع: ${e.message}")
        }
    }

    /**
     * إلغاء الاستماع
     */
    fun cancelListening() {
        try {
            speechRecognizer?.cancel()
            _isListening.value = false
            _recognitionState.value = RecognitionState.Idle
            Log.d(TAG, "تم إلغاء الاستماع")
        } catch (e: Exception) {
            Log.e(TAG, "خطأ في إلغاء الاستماع: ${e.message}")
        }
    }

    /**
     * حفظ نص محول كسجل
     */
    fun saveTranscription(text: String, language: String = "ar-SA", songTitle: String? = null) {
        val record = TranscriptionRecord(
            id = System.currentTimeMillis(),
            text = text,
            language = language,
            timestamp = System.currentTimeMillis(),
            songTitle = songTitle
        )
        val current = _transcriptions.value.toMutableList()
        current.add(0, record) // إضافة في البداية (الأحدث أولاً)
        _transcriptions.value = current
        Log.d(TAG, "تم حفظ التسجيل: ${text.take(50)}...")
    }

    /**
     * حذف سجل تحويل
     */
    fun deleteTranscription(id: Long) {
        _transcriptions.value = _transcriptions.value.filter { it.id != id }
    }

    /**
     * مسح جميع السجلات
     */
    fun clearAllTranscriptions() {
        _transcriptions.value = emptyList()
    }

    /**
     * تحرير الموارد
     */
    fun release() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        _isListening.value = false
        _recognitionState.value = RecognitionState.Idle
    }

    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "جاهز للاستماع")
            _recognitionState.value = RecognitionState.Listening
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "بدأ الكلام")
        }

        override fun onRmsChanged(rmsdB: Float) {
            // تحديث مستوى الصوت (يمكن استخدامه لعرض مؤشر الصوت)
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "انتهى الكلام")
            _isListening.value = false
        }

        override fun onError(error: Int) {
            _isListening.value = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "خطأ في الصوت"
                SpeechRecognizer.ERROR_CLIENT -> "خطأ في التطبيق"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "لا توجد صلاحية الميكروفون"
                SpeechRecognizer.ERROR_NETWORK -> "خطأ في الشبكة"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "انتهت مهلة الشبكة"
                SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التعرف على الكلام"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "خدمة التعرف مشغولة"
                SpeechRecognizer.ERROR_SERVER -> "خطأ في الخادم"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "لم يُكتشف كلام"
                else -> "خطأ غير معروف ($error)"
            }
            Log.e(TAG, "خطأ في التعرف: $errorMessage (كود: $error)")
            _recognitionState.value = RecognitionState.Error(errorMessage, error)
        }

        override fun onResults(results: Bundle?) {
            _isListening.value = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull() ?: ""
            if (text.isNotBlank()) {
                Log.d(TAG, "نتيجة نهائية: $text")
                _recognitionState.value = RecognitionState.FinalResult(text)
            } else {
                _recognitionState.value = RecognitionState.Error("لم يتم التعرف على أي كلام")
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull() ?: ""
            if (text.isNotBlank()) {
                _recognitionState.value = RecognitionState.PartialResult(text)
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}

/**
 * نموذج بيانات سجل التحويل
 */
data class TranscriptionRecord(
    val id: Long,
    val text: String,
    val language: String,
    val timestamp: Long,
    val songTitle: String? = null
) {
    fun getFormattedTime(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    fun getLanguageDisplay(): String = when (language) {
        "ar-SA", "ar" -> "عربي"
        "en-US", "en" -> "English"
        else -> language
    }
}

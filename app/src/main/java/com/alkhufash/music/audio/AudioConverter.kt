package com.alkhufash.music.audio

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * محول الصوت إلى صيغة Opus (المستخدمة في واتساب)
 * مقتبس من الكود المرسل من قبل المستخدم
 */
class AudioConverter(private val context: Context) {

    companion object {
        private const val TAG = "AudioConverter"
        
        const val TARGET_BITRATE = 20000 // 20 kbps
        const val TARGET_SAMPLE_RATE = 48000 // 48 kHz
        const val TARGET_CHANNELS = 1 // Mono
        const val OUTPUT_MIME_TYPE = "audio/opus"
        const val OUTPUT_FILE_EXTENSION = ".opus"
        
        val QUALITY_PRESETS = mapOf(
            "منخفضة" to 12000,
            "متوسطة" to 20000,
            "عالية" to 32000,
            "أقصى جودة" to 48000
        )
    }

    suspend fun convertToOpus(
        sourceUri: Uri,
        songName: String? = null,
        quality: String = "متوسطة",
        onProgress: (Int) -> Unit = {}
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            onProgress(10)
            val bitrate = QUALITY_PRESETS[quality] ?: TARGET_BITRATE
            
            val extractor = MediaExtractor()
            extractor.setDataSource(context, sourceUri, null)
            
            val audioTrackIndex = findAudioTrack(extractor)
            if (audioTrackIndex < 0) {
                return@withContext Result.failure(Exception("لا يوجد مسار صوتي في الملف"))
            }
            
            onProgress(30)
            val fileName = generateFileName(songName, quality.replace(" ", "_"))
            val outputFile = createOutputFile(fileName)
            
            // محاكاة عملية التحويل (تتطلب FFmpeg في البيئة الحقيقية)
            // في التطبيق الحقيقي، سيتم استخدام مكتبة FFmpeg-Android
            Log.d(TAG, "Converting $sourceUri to Opus at $bitrate bps")
            
            // محاكاة تقدم
            for (i in 4..9) {
                Thread.sleep(150)
                onProgress(i * 10)
            }
            
            // إنشاء ملف Opus (لأغراض العرض في هذا النموذج)
            outputFile.createNewFile()
            
            onProgress(100)
            Result.success(outputFile)

        } catch (e: Exception) {
            Log.e(TAG, "فشل التحويل", e)
            Result.failure(e)
        }
    }

    private fun findAudioTrack(extractor: MediaExtractor): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("audio/")) return i
        }
        return -1
    }

    private fun generateFileName(originalName: String?, suffix: String): String {
        val baseName = originalName?.substringBeforeLast(".")?.replace(" ", "_") ?: "recording"
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${baseName}_${suffix}_${timestamp}${OUTPUT_FILE_EXTENSION}"
    }

    private fun createOutputFile(fileName: String): File {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "WhatsAppOpus")
        if (!directory.exists()) directory.mkdirs()
        return File(directory, fileName)
    }
}

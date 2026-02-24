package com.alkhufash.music.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alkhufash.music.service.MusicController
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

@HiltWorker
class SleepTimerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val musicController: MusicController
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "sleep_timer_work"
        private const val TAG = "SleepTimerWorker"
        private const val MAX_RETRY_ATTEMPTS = 5
        private const val RETRY_DELAY_MS = 1000L
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "SleepTimerWorker: بدء تنفيذ مؤقت الإيقاف")

        // محاولة الإيقاف مع إعادة المحاولة في حالة عدم الاتصال
        val success = withTimeoutOrNull(10_000L) {
            var attempts = 0
            while (attempts < MAX_RETRY_ATTEMPTS) {
                try {
                    musicController.pause()
                    Log.d(TAG, "SleepTimerWorker: تم إيقاف التشغيل بنجاح في المحاولة ${attempts + 1}")
                    return@withTimeoutOrNull true
                } catch (e: Exception) {
                    Log.w(TAG, "SleepTimerWorker: محاولة ${attempts + 1} فشلت: ${e.message}")
                    attempts++
                    if (attempts < MAX_RETRY_ATTEMPTS) delay(RETRY_DELAY_MS)
                }
            }
            false
        }

        // إرسال broadcast كبديل في حالة فشل الاتصال المباشر
        if (success != true) {
            Log.w(TAG, "SleepTimerWorker: إرسال broadcast للإيقاف كبديل")
            val intent = Intent("com.alkhufash.music.PAUSE_PLAYBACK").apply {
                setPackage(applicationContext.packageName)
            }
            applicationContext.sendBroadcast(intent)
        }

        return Result.success()
    }
}

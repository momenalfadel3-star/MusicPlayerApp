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
class StartTimerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val musicController: MusicController
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "start_timer_work"
        private const val TAG = "StartTimerWorker"
        private const val MAX_RETRY_ATTEMPTS = 5
        private const val RETRY_DELAY_MS = 1000L
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "StartTimerWorker: بدء تنفيذ مؤقت التشغيل التلقائي")

        // محاولة التشغيل مع إعادة المحاولة في حالة عدم الاتصال
        val success = withTimeoutOrNull(10_000L) {
            var attempts = 0
            while (attempts < MAX_RETRY_ATTEMPTS) {
                try {
                    musicController.play()
                    Log.d(TAG, "StartTimerWorker: تم بدء التشغيل بنجاح في المحاولة ${attempts + 1}")
                    return@withTimeoutOrNull true
                } catch (e: Exception) {
                    Log.w(TAG, "StartTimerWorker: محاولة ${attempts + 1} فشلت: ${e.message}")
                    attempts++
                    if (attempts < MAX_RETRY_ATTEMPTS) delay(RETRY_DELAY_MS)
                }
            }
            false
        }

        // إرسال broadcast كبديل في حالة فشل الاتصال المباشر
        if (success != true) {
            Log.w(TAG, "StartTimerWorker: إرسال broadcast للتشغيل كبديل")
            val intent = Intent("com.alkhufash.music.START_PLAYBACK").apply {
                setPackage(applicationContext.packageName)
            }
            applicationContext.sendBroadcast(intent)
        }

        return Result.success()
    }
}

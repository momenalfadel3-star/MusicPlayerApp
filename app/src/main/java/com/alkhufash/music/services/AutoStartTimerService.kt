package com.alkhufash.music.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alkhufash.music.R
import com.alkhufash.music.receiver.PlaybackBroadcastReceiver
import com.alkhufash.music.service.MusicController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AutoStartTimerService : Service() {

    @Inject
    lateinit var musicController: MusicController
    
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis = 0L
    
    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
        private const val TAG = "AutoStartTimerService"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val minutes = intent?.getIntExtra("timer_minutes", 30) ?: 30
        timeLeftInMillis = minutes * 60 * 1000L

        Log.d(TAG, "بدء مؤقت التشغيل التلقائي: $minutes دقيقة")

        startCountdown()
        startForeground(NOTIFICATION_ID, createNotification("العد التنازلي: ${formatTime(timeLeftInMillis)}"))

        return START_NOT_STICKY
    }
    
    private fun startCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateNotification("العد التنازلي: ${formatTime(millisUntilFinished)}")
            }

            override fun onFinish() {
                Log.d(TAG, "انتهى العد التنازلي - بدء التشغيل")
                startPlayback()
                stopSelf()
            }
        }.start()
    }
    
    private fun formatTime(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (millis % (1000 * 60)) / 1000
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    private fun startPlayback() {
        // محاولة التشغيل المباشر عبر MusicController
        try {
            musicController.play()
            Log.d(TAG, "تم بدء التشغيل مباشرة عبر MusicController")
        } catch (e: Exception) {
            Log.w(TAG, "فشل التشغيل المباشر، إرسال broadcast: ${e.message}")
            // إرسال broadcast كبديل
            val intent = Intent(PlaybackBroadcastReceiver.ACTION_START_PLAYBACK).apply {
                setPackage(packageName)
            }
            sendBroadcast(intent)
        }
    }
    
    private fun createNotification(text: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("مؤقت التشغيل التلقائي")
        .setContentText(text)
        .setSmallIcon(R.drawable.ic_timer)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(text))
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for auto start timer countdown"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}

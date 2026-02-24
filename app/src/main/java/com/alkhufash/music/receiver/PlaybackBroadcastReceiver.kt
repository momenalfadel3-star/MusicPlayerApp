package com.alkhufash.music.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.alkhufash.music.service.MusicController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver لاستقبال أوامر التشغيل والإيقاف من Workers والخدمات
 * يُستخدم كبديل عند فشل الاتصال المباشر بـ MediaController
 */
@AndroidEntryPoint
class PlaybackBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var musicController: MusicController

    companion object {
        const val ACTION_START_PLAYBACK = "com.alkhufash.music.START_PLAYBACK"
        const val ACTION_PAUSE_PLAYBACK = "com.alkhufash.music.PAUSE_PLAYBACK"
        private const val TAG = "PlaybackReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "استقبال broadcast: ${intent.action}")
        when (intent.action) {
            ACTION_START_PLAYBACK -> {
                try {
                    musicController.play()
                    Log.d(TAG, "تم تشغيل الموسيقى عبر broadcast")
                } catch (e: Exception) {
                    Log.e(TAG, "فشل تشغيل الموسيقى عبر broadcast: ${e.message}")
                }
            }
            ACTION_PAUSE_PLAYBACK -> {
                try {
                    musicController.pause()
                    Log.d(TAG, "تم إيقاف الموسيقى عبر broadcast")
                } catch (e: Exception) {
                    Log.e(TAG, "فشل إيقاف الموسيقى عبر broadcast: ${e.message}")
                }
            }
        }
    }
}

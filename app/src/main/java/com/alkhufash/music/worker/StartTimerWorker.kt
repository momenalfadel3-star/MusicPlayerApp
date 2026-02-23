package com.alkhufash.music.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alkhufash.music.service.MusicController
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StartTimerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val musicController: MusicController
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        musicController.play()
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "start_timer_work"
    }
}

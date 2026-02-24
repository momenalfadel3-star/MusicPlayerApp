package com.alkhufash.music.service

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alkhufash.music.domain.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var isInitialized = false

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    companion object {
        private const val TAG = "MusicController"
    }

    val isConnected: Boolean
        get() = mediaController != null && mediaController?.isConnected == true

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let {
                _playerState.value = _playerState.value.copy(
                    currentSongTitle = it.mediaMetadata.title?.toString() ?: "",
                    currentSongArtist = it.mediaMetadata.artist?.toString() ?: "",
                    currentMediaId = it.mediaId
                )
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playerState.value = _playerState.value.copy(
                playbackState = playbackState
            )
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _playerState.value = _playerState.value.copy(isShuffleOn = shuffleModeEnabled)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _playerState.value = _playerState.value.copy(repeatMode = repeatMode)
        }
    }

    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        Log.d(TAG, "تهيئة MusicController")
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.addListener(playerListener)
                Log.d(TAG, "تم الاتصال بـ MediaSession بنجاح")
            } catch (e: Exception) {
                Log.e(TAG, "فشل الاتصال بـ MediaSession: ${e.message}")
                isInitialized = false
            }
        }, MoreExecutors.directExecutor())
    }

    fun release() {
        mediaController?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
        isInitialized = false
        Log.d(TAG, "تم تحرير MusicController")
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setArtworkUri(
                            android.net.Uri.parse("content://media/external/audio/albumart/${song.albumId}")
                        )
                        .build()
                )
                .build()
        }
        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
    }

    fun play() {
        if (!isConnected) {
            Log.w(TAG, "play() استدعاء قبل الاتصال - تجاهل")
            // تهيئة تلقائية إذا لم يكن متصلاً
            if (!isInitialized) initialize()
        }
        mediaController?.play()
    }

    fun pause() {
        if (!isConnected) {
            Log.w(TAG, "pause() استدعاء قبل الاتصال - تجاهل")
        }
        mediaController?.pause()
    }
    fun playOrPause() {
        if (mediaController?.isPlaying == true) pause() else play()
    }

    fun seekToNext() { mediaController?.seekToNextMediaItem() }
    fun seekToPrevious() { mediaController?.seekToPreviousMediaItem() }
    fun seekTo(position: Long) { mediaController?.seekTo(position) }

    fun toggleShuffle() {
        val current = mediaController?.shuffleModeEnabled ?: false
        mediaController?.shuffleModeEnabled = !current
    }

    fun toggleRepeat() {
        val current = mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF
        val next = when (current) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        mediaController?.repeatMode = next
    }

    fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L
    fun getDuration(): Long = mediaController?.duration ?: 0L
    fun isPlaying(): Boolean = mediaController?.isPlaying ?: false

    fun getCurrentMediaItemIndex(): Int = mediaController?.currentMediaItemIndex ?: 0
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSongTitle: String = "",
    val currentSongArtist: String = "",
    val currentMediaId: String = "",
    val playbackState: Int = Player.STATE_IDLE,
    val isShuffleOn: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF
)

package com.alkhufash.music.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alkhufash.music.domain.model.Album
import com.alkhufash.music.domain.model.Artist
import com.alkhufash.music.domain.model.Folder
import com.alkhufash.music.domain.model.Playlist
import com.alkhufash.music.domain.model.Song
import com.alkhufash.music.domain.repository.MusicRepository
import com.alkhufash.music.service.MusicController
import com.alkhufash.music.service.PlayerState
import com.alkhufash.music.worker.SleepTimerWorker
import com.alkhufash.music.worker.StartTimerWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val musicController: MusicController,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    // Player State from controller
    val playerState: StateFlow<PlayerState> = musicController.playerState

    // Current position for seek bar
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var positionJob: Job? = null

    // Timer state
    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private val _startTimerMinutes = MutableStateFlow(0)
    val startTimerMinutes: StateFlow<Int> = _startTimerMinutes.asStateFlow()

    private val _isSleepTimerActive = MutableStateFlow(false)
    val isSleepTimerActive: StateFlow<Boolean> = _isSleepTimerActive.asStateFlow()

    private val _isStartTimerActive = MutableStateFlow(false)
    val isStartTimerActive: StateFlow<Boolean> = _isStartTimerActive.asStateFlow()

    init {
        loadAllData()
        startPositionTracking()
    }

    private fun loadAllData() {
        loadSongs()
        loadAlbums()
        loadArtists()
        loadFolders()
        loadPlaylists()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getAllSongs()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { songs ->
                    _uiState.value = _uiState.value.copy(
                        songs = songs,
                        isLoading = false
                    )
                }
        }
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            repository.getAllAlbums().collect { albums ->
                _uiState.value = _uiState.value.copy(albums = albums)
            }
        }
    }

    private fun loadArtists() {
        viewModelScope.launch {
            repository.getAllArtists().collect { artists ->
                _uiState.value = _uiState.value.copy(artists = artists)
            }
        }
    }

    private fun loadFolders() {
        viewModelScope.launch {
            repository.getAllFolders().collect { folders ->
                _uiState.value = _uiState.value.copy(folders = folders)
            }
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            repository.getAllPlaylists().collect { playlists ->
                _uiState.value = _uiState.value.copy(playlists = playlists)
            }
        }
    }

    private fun startPositionTracking() {
        positionJob = viewModelScope.launch {
            while (true) {
                _currentPosition.value = musicController.getCurrentPosition()
                _duration.value = musicController.getDuration()
                delay(500)
            }
        }
    }

    // Playback Controls
    fun playSong(song: Song) {
        val songs = _uiState.value.songs
        val index = songs.indexOfFirst { it.id == song.id }
        if (index >= 0) {
            musicController.playSongs(songs, index)
            _uiState.value = _uiState.value.copy(currentSong = song)
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        musicController.playSongs(songs, startIndex)
        if (songs.isNotEmpty() && startIndex < songs.size) {
            _uiState.value = _uiState.value.copy(currentSong = songs[startIndex])
        }
    }

    fun playOrPause() = musicController.playOrPause()
    fun seekToNext() = musicController.seekToNext()
    fun seekToPrevious() = musicController.seekToPrevious()
    fun seekTo(position: Long) = musicController.seekTo(position)
    fun toggleShuffle() = musicController.toggleShuffle()
    fun toggleRepeat() = musicController.toggleRepeat()

    // Search
    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(searchResults = emptyList(), searchQuery = "")
            } else {
                val results = repository.searchSongs(query)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    searchQuery = query
                )
            }
        }
    }

    // Favorites
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(songId)
        }
    }

    // Playlists
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun addSongToPlaylist(songId: Long, playlistId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(songId, playlistId)
        }
    }

    // Timer Functions
    fun setSleepTimer(minutes: Int) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(SleepTimerWorker.WORK_NAME)

        if (minutes > 0) {
            val request = OneTimeWorkRequestBuilder<SleepTimerWorker>()
                .setInitialDelay(minutes.toLong(), TimeUnit.MINUTES)
                .build()
            workManager.enqueueUniqueWork(
                SleepTimerWorker.WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                request
            )
            _isSleepTimerActive.value = true
            _sleepTimerMinutes.value = minutes
        } else {
            _isSleepTimerActive.value = false
            _sleepTimerMinutes.value = 0
        }
    }

    fun setStartTimer(minutes: Int) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(StartTimerWorker.WORK_NAME)

        if (minutes > 0) {
            val request = OneTimeWorkRequestBuilder<StartTimerWorker>()
                .setInitialDelay(minutes.toLong(), TimeUnit.MINUTES)
                .build()
            workManager.enqueueUniqueWork(
                StartTimerWorker.WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                request
            )
            _isStartTimerActive.value = true
            _startTimerMinutes.value = minutes
        } else {
            _isStartTimerActive.value = false
            _startTimerMinutes.value = 0
        }
    }

    fun cancelSleepTimer() = setSleepTimer(0)
    fun cancelStartTimer() = setStartTimer(0)

    // Album songs
    fun getAlbumSongs(albumId: Long): List<Song> {
        return _uiState.value.songs.filter { it.albumId == albumId }
    }

    // Folder songs
    fun getFolderSongs(folderPath: String): List<Song> {
        return _uiState.value.songs.filter { it.path.startsWith(folderPath) }
    }

    override fun onCleared() {
        super.onCleared()
        positionJob?.cancel()
    }
}

data class MusicUiState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val currentSong: Song? = null,
    val searchResults: List<Song> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

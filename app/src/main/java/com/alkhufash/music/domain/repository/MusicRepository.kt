package com.alkhufash.music.domain.repository

import com.alkhufash.music.domain.model.Album
import com.alkhufash.music.domain.model.Artist
import com.alkhufash.music.domain.model.Folder
import com.alkhufash.music.domain.model.Playlist
import com.alkhufash.music.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    // Songs
    fun getAllSongs(): Flow<List<Song>>
    suspend fun getSongById(id: Long): Song?
    suspend fun searchSongs(query: String): List<Song>
    suspend fun getSongsByAlbum(albumId: Long): List<Song>
    suspend fun getSongsByArtist(artistId: Long): List<Song>
    suspend fun getSongsByFolder(folderPath: String): List<Song>
    fun getFavoriteSongs(): Flow<List<Song>>

    // Albums
    fun getAllAlbums(): Flow<List<Album>>

    // Artists
    fun getAllArtists(): Flow<List<Artist>>

    // Folders
    fun getAllFolders(): Flow<List<Folder>>

    // Playlists
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongToPlaylist(songId: Long, playlistId: Long)
    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long)
    suspend fun getPlaylistSongs(playlistId: Long): List<Song>

    // Favorites
    suspend fun toggleFavorite(songId: Long)
    suspend fun isFavorite(songId: Long): Boolean
}

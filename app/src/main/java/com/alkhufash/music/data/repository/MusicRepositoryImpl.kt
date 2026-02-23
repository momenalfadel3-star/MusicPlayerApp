package com.alkhufash.music.data.repository

import com.alkhufash.music.data.db.FavoriteEntity
import com.alkhufash.music.data.db.MusicDao
import com.alkhufash.music.data.db.PlaylistEntity
import com.alkhufash.music.data.db.PlaylistSongEntity
import com.alkhufash.music.data.model.MediaStoreHelper
import com.alkhufash.music.domain.model.Album
import com.alkhufash.music.domain.model.Artist
import com.alkhufash.music.domain.model.Folder
import com.alkhufash.music.domain.model.Playlist
import com.alkhufash.music.domain.model.Song
import com.alkhufash.music.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreHelper: MediaStoreHelper,
    private val musicDao: MusicDao
) : MusicRepository {

    private var cachedSongs: List<Song> = emptyList()

    override fun getAllSongs(): Flow<List<Song>> = flow {
        val songs = mediaStoreHelper.getAllSongs()
        cachedSongs = songs
        val favorites = musicDao.getAllFavorites()
        favorites.collect { favList ->
            val favIds = favList.map { it.songId }.toSet()
            emit(songs.map { it.copy(isFavorite = it.id in favIds) })
        }
    }

    override suspend fun getSongById(id: Long): Song? {
        return cachedSongs.find { it.id == id }
    }

    override suspend fun searchSongs(query: String): List<Song> {
        val lowerQuery = query.lowercase()
        return cachedSongs.filter {
            it.title.lowercase().contains(lowerQuery) ||
                    it.artist.lowercase().contains(lowerQuery) ||
                    it.album.lowercase().contains(lowerQuery)
        }
    }

    override suspend fun getSongsByAlbum(albumId: Long): List<Song> {
        return cachedSongs.filter { it.albumId == albumId }
            .sortedBy { it.trackNumber }
    }

    override suspend fun getSongsByArtist(artistId: Long): List<Song> {
        // Filter by artist name since we store artist name in Song
        return cachedSongs
    }

    override suspend fun getSongsByFolder(folderPath: String): List<Song> {
        return cachedSongs.filter { it.path.startsWith(folderPath) }
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return musicDao.getAllFavorites().map { favorites ->
            val favIds = favorites.map { it.songId }.toSet()
            cachedSongs.filter { it.id in favIds }.map { it.copy(isFavorite = true) }
        }
    }

    override fun getAllAlbums(): Flow<List<Album>> = flow {
        emit(mediaStoreHelper.getAllAlbums())
    }

    override fun getAllArtists(): Flow<List<Artist>> = flow {
        emit(mediaStoreHelper.getAllArtists())
    }

    override fun getAllFolders(): Flow<List<Folder>> = flow {
        val songs = if (cachedSongs.isEmpty()) mediaStoreHelper.getAllSongs() else cachedSongs
        emit(mediaStoreHelper.getFolders(songs))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return musicDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                val count = musicDao.getPlaylistSongCount(entity.id)
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    songCount = count,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun createPlaylist(name: String): Long {
        return musicDao.insertPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        musicDao.clearPlaylist(playlistId)
        musicDao.deletePlaylist(PlaylistEntity(id = playlistId, name = ""))
    }

    override suspend fun addSongToPlaylist(songId: Long, playlistId: Long) {
        val count = musicDao.getPlaylistSongCount(playlistId)
        musicDao.addSongToPlaylist(
            PlaylistSongEntity(
                playlistId = playlistId,
                songId = songId,
                position = count
            )
        )
    }

    override suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long) {
        musicDao.removeSongFromPlaylist(playlistId, songId)
    }

    override suspend fun getPlaylistSongs(playlistId: Long): List<Song> {
        val songIds = musicDao.getPlaylistSongIds(playlistId)
        return songIds.mapNotNull { id -> cachedSongs.find { it.id == id } }
    }

    override suspend fun toggleFavorite(songId: Long) {
        if (musicDao.isFavorite(songId)) {
            musicDao.removeFavorite(FavoriteEntity(songId))
        } else {
            musicDao.addFavorite(FavoriteEntity(songId))
        }
    }

    override suspend fun isFavorite(songId: Long): Boolean {
        return musicDao.isFavorite(songId)
    }
}

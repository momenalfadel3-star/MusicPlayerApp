package com.alkhufash.music.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alkhufash.music.domain.model.Song
import com.alkhufash.music.presentation.components.MiniPlayer
import com.alkhufash.music.presentation.components.SongItem
import com.alkhufash.music.presentation.theme.MusicPink
import com.alkhufash.music.presentation.theme.MusicPurple
import com.alkhufash.music.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onSongClick: (Song) -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val tabs = listOf("Songs", "Albums", "Artists", "Playlists", "Folders")

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.search(it)
                    },
                    onSearch = { viewModel.search(it) },
                    active = true,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text("Search songs, artists...") },
                    leadingIcon = {
                        IconButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn {
                        items(uiState.searchResults) { song ->
                            SongItem(
                                song = song,
                                isPlaying = playerState.currentMediaId == song.id.toString(),
                                onClick = { onSongClick(song) },
                                onFavoriteClick = { viewModel.toggleFavorite(song.id) }
                            )
                        }
                    }
                }
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "Music",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, "Search")
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            Column {
                MiniPlayer(
                    currentSong = uiState.currentSong,
                    isPlaying = playerState.isPlaying,
                    onPlayPauseClick = { viewModel.playOrPause() },
                    onNextClick = { viewModel.seekToNext() },
                    onPlayerClick = onNavigateToNowPlaying
                )
                NavigationBar {
                    // Navigation bar is handled by tabs above
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> SongsTab(
                    songs = uiState.songs,
                    currentMediaId = playerState.currentMediaId,
                    onSongClick = onSongClick,
                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                    isLoading = uiState.isLoading
                )
                1 -> AlbumsTab(
                    albums = uiState.albums,
                    onAlbumClick = { album ->
                        val songs = viewModel.getAlbumSongs(album.id)
                        if (songs.isNotEmpty()) {
                            viewModel.playSongs(songs)
                            onNavigateToNowPlaying()
                        }
                    }
                )
                2 -> ArtistsTab(artists = uiState.artists)
                3 -> PlaylistsTab(
                    playlists = uiState.playlists,
                    onCreatePlaylist = { viewModel.createPlaylist(it) }
                )
                4 -> FoldersTab(
                    folders = uiState.folders,
                    onFolderClick = { folder ->
                        val songs = viewModel.getFolderSongs(folder.path)
                        if (songs.isNotEmpty()) {
                            viewModel.playSongs(songs)
                            onNavigateToNowPlaying()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SongsTab(
    songs: List<Song>,
    currentMediaId: String,
    onSongClick: (Song) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No songs found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Add music files to your device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(songs, key = { it.id }) { song ->
            SongItem(
                song = song,
                isPlaying = currentMediaId == song.id.toString(),
                onClick = { onSongClick(song) },
                onFavoriteClick = { onFavoriteClick(song.id) }
            )
        }
    }
}

@Composable
fun AlbumsTab(
    albums: List<com.alkhufash.music.domain.model.Album>,
    onAlbumClick: (com.alkhufash.music.domain.model.Album) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums, key = { it.id }) { album ->
            AlbumCard(album = album, onClick = { onAlbumClick(album) })
        }
    }
}

@Composable
fun AlbumCard(
    album: com.alkhufash.music.domain.model.Album,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "content://media/external/audio/albumart/${album.id}",
                    contentDescription = album.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    Icons.Default.Album,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${album.artist} • ${album.songCount} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ArtistsTab(artists: List<com.alkhufash.music.domain.model.Artist>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(artists, key = { it.id }) { artist ->
            ListItem(
                headlineContent = {
                    Text(artist.name, style = MaterialTheme.typography.titleSmall)
                },
                supportingContent = {
                    Text(
                        "${artist.albumCount} albums • ${artist.songCount} songs",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(MusicPurple, MusicPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun PlaylistsTab(
    playlists: List<com.alkhufash.music.domain.model.Playlist>,
    onCreatePlaylist: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        onCreatePlaylist(newPlaylistName)
                        newPlaylistName = ""
                        showCreateDialog = false
                    }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            OutlinedButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Playlist")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(playlists, key = { it.id }) { playlist ->
            ListItem(
                headlineContent = { Text(playlist.name) },
                supportingContent = { Text("${playlist.songCount} songs") },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlaylistPlay, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun FoldersTab(
    folders: List<com.alkhufash.music.domain.model.Folder>,
    onFolderClick: (com.alkhufash.music.domain.model.Folder) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(folders, key = { it.path }) { folder ->
            ListItem(
                headlineContent = { Text(folder.name) },
                supportingContent = { Text("${folder.songCount} songs") },
                leadingContent = {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                },
                modifier = Modifier.clickable { onFolderClick(folder) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

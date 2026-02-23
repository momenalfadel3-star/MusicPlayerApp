package com.alkhufash.music.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alkhufash.music.presentation.screens.equalizer.EqualizerScreen
import com.alkhufash.music.presentation.screens.home.HomeScreen
import com.alkhufash.music.presentation.screens.nowplaying.NowPlayingScreen
import com.alkhufash.music.presentation.screens.settings.SettingsScreen
import com.alkhufash.music.presentation.screens.timer.TimerScreen
import com.alkhufash.music.presentation.viewmodel.MusicViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object NowPlaying : Screen("now_playing")
    object Timer : Screen("timer")
    object Equalizer : Screen("equalizer")
    object Settings : Screen("settings")
    object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    object ArtistDetail : Screen("artist/{artistId}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
    object FolderDetail : Screen("folder/{folderPath}") {
        fun createRoute(folderPath: String) = "folder/${folderPath.replace("/", "|")}"
    }
}

@Composable
fun MusicNavHost(
    navController: NavHostController,
    viewModel: MusicViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onSongClick = { song ->
                    viewModel.playSong(song)
                    navController.navigate(Screen.NowPlaying.route)
                },
                onNavigateToNowPlaying = {
                    navController.navigate(Screen.NowPlaying.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTimer = { navController.navigate(Screen.Timer.route) },
                onNavigateToEqualizer = { navController.navigate(Screen.Equalizer.route) }
            )
        }

        composable(Screen.Timer.route) {
            TimerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Equalizer.route) {
            EqualizerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

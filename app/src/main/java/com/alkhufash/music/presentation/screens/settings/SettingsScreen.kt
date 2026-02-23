package com.alkhufash.music.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var isDarkMode by remember { mutableStateOf(true) }
    var useDynamicColor by remember { mutableStateOf(true) }
    var showLyrics by remember { mutableStateOf(false) }
    var crossfadeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance Section
            SettingsSectionHeader("Appearance")

            SettingsToggleItem(
                title = "Dark Mode",
                subtitle = "Use dark theme",
                icon = Icons.Default.DarkMode,
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )

            SettingsToggleItem(
                title = "Dynamic Colors",
                subtitle = "Adapt colors to your wallpaper (Android 12+)",
                icon = Icons.Default.Palette,
                checked = useDynamicColor,
                onCheckedChange = { useDynamicColor = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Playback Section
            SettingsSectionHeader("Playback")

            SettingsToggleItem(
                title = "Crossfade",
                subtitle = "Smooth transition between songs",
                icon = Icons.Default.Tune,
                checked = crossfadeEnabled,
                onCheckedChange = { crossfadeEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // About Section
            SettingsSectionHeader("About")

            ListItem(
                headlineContent = { Text("Version") },
                supportingContent = { Text("1.0.0") },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            )

            ListItem(
                headlineContent = { Text("Developer") },
                supportingContent = { Text("AlKhufash") },
                leadingContent = {
                    Icon(Icons.Default.Person, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            )

            ListItem(
                headlineContent = { Text("License") },
                supportingContent = { Text("MIT License") },
                leadingContent = {
                    Icon(Icons.Default.Description, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            )

            ListItem(
                headlineContent = { Text("Built with") },
                supportingContent = { Text("Kotlin • Jetpack Compose • ExoPlayer • Material Design 3") },
                leadingContent = {
                    Icon(Icons.Default.Code, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

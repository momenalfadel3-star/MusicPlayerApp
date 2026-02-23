package com.alkhufash.music.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alkhufash.music.R
import com.alkhufash.music.presentation.theme.BatCyan
import com.alkhufash.music.presentation.theme.BatOrange
import com.alkhufash.music.presentation.theme.BatPink
import com.alkhufash.music.presentation.theme.BatPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var isDarkMode by remember { mutableStateOf(true) }
    var useDynamicColor by remember { mutableStateOf(false) }
    var crossfadeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.cancel))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // بطاقة التطبيق العلوية
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(BatPurple, BatPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = BatPurple
                        )
                        Text(
                            "${stringResource(R.string.version)} 2.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            stringResource(R.string.alkhufash),
                            style = MaterialTheme.typography.labelMedium,
                            color = BatPink
                        )
                    }
                }
            }

            // قسم المظهر
            SettingsSectionHeader(
                title = stringResource(R.string.appearance),
                icon = Icons.Default.Palette,
                color = BatPurple
            )

            SettingsToggleItem(
                title = stringResource(R.string.dark_mode),
                subtitle = stringResource(R.string.dark_mode_subtitle),
                icon = Icons.Default.DarkMode,
                iconColor = BatPurple,
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )

            SettingsToggleItem(
                title = stringResource(R.string.dynamic_colors),
                subtitle = stringResource(R.string.dynamic_colors_subtitle),
                icon = Icons.Default.Palette,
                iconColor = BatPink,
                checked = useDynamicColor,
                onCheckedChange = { useDynamicColor = it }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // قسم التشغيل
            SettingsSectionHeader(
                title = stringResource(R.string.playback),
                icon = Icons.Default.PlayArrow,
                color = BatOrange
            )

            SettingsToggleItem(
                title = stringResource(R.string.crossfade),
                subtitle = stringResource(R.string.crossfade_subtitle),
                icon = Icons.Default.Tune,
                iconColor = BatOrange,
                checked = crossfadeEnabled,
                onCheckedChange = { crossfadeEnabled = it }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // قسم حول التطبيق
            SettingsSectionHeader(
                title = stringResource(R.string.about),
                icon = Icons.Default.Info,
                color = BatCyan
            )

            SettingsInfoItem(
                title = stringResource(R.string.version),
                subtitle = "2.0.0",
                icon = Icons.Default.Info,
                iconColor = BatCyan
            )

            SettingsInfoItem(
                title = stringResource(R.string.developer),
                subtitle = stringResource(R.string.alkhufash),
                icon = Icons.Default.Person,
                iconColor = BatPurple
            )

            SettingsInfoItem(
                title = stringResource(R.string.license),
                subtitle = stringResource(R.string.mit_license),
                icon = Icons.Default.Description,
                iconColor = BatPink
            )

            SettingsInfoItem(
                title = stringResource(R.string.built_with),
                subtitle = stringResource(R.string.built_with_tech),
                icon = Icons.Default.Code,
                iconColor = BatOrange
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(title, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = iconColor
                )
            )
        }
    )
}

@Composable
fun SettingsInfoItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color
) {
    ListItem(
        headlineContent = {
            Text(title, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    )
}

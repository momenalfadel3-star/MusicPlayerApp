package com.alkhufash.music.presentation.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkhufash.music.presentation.theme.MusicOrange
import com.alkhufash.music.presentation.theme.MusicPink
import com.alkhufash.music.presentation.theme.MusicPurple
import com.alkhufash.music.presentation.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: MusicViewModel,
    onNavigateBack: () -> Unit
) {
    val isSleepTimerActive by viewModel.isSleepTimerActive.collectAsStateWithLifecycle()
    val isStartTimerActive by viewModel.isStartTimerActive.collectAsStateWithLifecycle()
    val sleepMinutes by viewModel.sleepTimerMinutes.collectAsStateWithLifecycle()
    val startMinutes by viewModel.startTimerMinutes.collectAsStateWithLifecycle()

    var sleepTimerValue by remember { mutableFloatStateOf(30f) }
    var startTimerValue by remember { mutableFloatStateOf(30f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timer") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sleep Timer Card
            TimerCard(
                title = "Sleep Timer",
                subtitle = "Stop playback after",
                icon = Icons.Default.NightShelter,
                gradientColors = listOf(MusicPurple, MusicPink),
                isActive = isSleepTimerActive,
                activeMinutes = sleepMinutes,
                sliderValue = sleepTimerValue,
                onSliderChange = { sleepTimerValue = it },
                onSetTimer = { viewModel.setSleepTimer(sleepTimerValue.toInt()) },
                onCancelTimer = { viewModel.cancelSleepTimer() }
            )

            // Start Timer Card
            TimerCard(
                title = "Start Timer",
                subtitle = "Begin playback after",
                icon = Icons.Default.PlayCircle,
                gradientColors = listOf(MusicOrange, MusicPink),
                isActive = isStartTimerActive,
                activeMinutes = startMinutes,
                sliderValue = startTimerValue,
                onSliderChange = { startTimerValue = it },
                onSetTimer = { viewModel.setStartTimer(startTimerValue.toInt()) },
                onCancelTimer = { viewModel.cancelStartTimer() }
            )

            // Quick presets
            Text(
                "Quick Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 45, 60).forEach { minutes ->
                    FilterChip(
                        selected = false,
                        onClick = { sleepTimerValue = minutes.toFloat() },
                        label = { Text("${minutes}m") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TimerCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradientColors: List<Color>,
    isActive: Boolean,
    activeMinutes: Int,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    onSetTimer: () -> Unit,
    onCancelTimer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(gradientColors),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isActive) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text("${activeMinutes}m")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isActive) {
                // Slider
                Text(
                    "${sliderValue.toInt()} minutes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = gradientColors.first()
                )

                Slider(
                    value = sliderValue,
                    onValueChange = onSliderChange,
                    valueRange = 5f..120f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = gradientColors.first(),
                        activeTrackColor = gradientColors.first()
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("5 min", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("2 hours", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSetTimer,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gradientColors.first()
                    )
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Timer")
                }
            } else {
                // Active state
                Text(
                    "Timer active: ${activeMinutes} minutes remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancelTimer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Timer")
                }
            }
        }
    }
}

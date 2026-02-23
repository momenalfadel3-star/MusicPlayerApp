package com.alkhufash.music.presentation.screens.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    onNavigateBack: () -> Unit
) {
    val presets = listOf("Normal", "Pop", "Rock", "Jazz", "Classical", "Hip-Hop", "Electronic", "Bass Boost", "Treble Boost", "Vocal")
    var selectedPreset by remember { mutableStateOf("Normal") }

    // EQ bands: 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
    val bands = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
    val bandValues = remember { mutableStateListOf(0f, 0f, 0f, 0f, 0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equalizer") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Presets
            Text("Presets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Preset chips in a wrapped row
            val chunkedPresets = presets.chunked(3)
            chunkedPresets.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { preset ->
                        FilterChip(
                            selected = selectedPreset == preset,
                            onClick = {
                                selectedPreset = preset
                                applyPreset(preset, bandValues)
                            },
                            label = { Text(preset) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // EQ Bands
            Text("Custom", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            bands.forEachIndexed { index, band ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = band,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(60.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = bandValues[index],
                        onValueChange = {
                            bandValues[index] = it
                            selectedPreset = "Custom"
                        },
                        valueRange = -10f..10f,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${bandValues[index].toInt()}dB",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reset button
            OutlinedButton(
                onClick = {
                    bandValues.indices.forEach { bandValues[it] = 0f }
                    selectedPreset = "Normal"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset to Normal")
            }
        }
    }
}

private fun applyPreset(preset: String, bands: MutableList<Float>) {
    val values = when (preset) {
        "Pop" -> listOf(-1f, 2f, 4f, 3f, 1f)
        "Rock" -> listOf(4f, 2f, -1f, 2f, 4f)
        "Jazz" -> listOf(3f, 2f, 0f, 2f, 3f)
        "Classical" -> listOf(4f, 3f, -2f, 3f, 4f)
        "Hip-Hop" -> listOf(5f, 3f, 0f, -1f, 2f)
        "Electronic" -> listOf(4f, 2f, 0f, 2f, 4f)
        "Bass Boost" -> listOf(6f, 4f, 0f, -2f, -2f)
        "Treble Boost" -> listOf(-2f, -2f, 0f, 4f, 6f)
        "Vocal" -> listOf(-2f, 0f, 4f, 3f, -1f)
        else -> listOf(0f, 0f, 0f, 0f, 0f)
    }
    values.forEachIndexed { i, v -> bands[i] = v }
}

package com.alkhufash.music.presentation.screens.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun EqualizerScreen(
    onNavigateBack: () -> Unit
) {
    val presets = listOf("عادي", "بوب", "روك", "جاز", "كلاسيك", "هيب هوب", "إلكترونيك", "باس", "ترِبل", "صوتي")
    var selectedPreset by remember { mutableStateOf("عادي") }

    // نطاقات التردد
    val bands = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
    val bandValues = remember { mutableStateListOf(0f, 0f, 0f, 0f, 0f) }

    // ألوان النطاقات
    val bandColors = listOf(BatPurple, BatPink, BatOrange, BatCyan, BatPurple)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.equalizer),
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // الإعدادات المسبقة
            Text(
                stringResource(R.string.presets),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

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
                            label = { Text(preset, fontWeight = if (selectedPreset == preset) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BatPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // نطاقات مخصصة
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.custom),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // زر إعادة التعيين
                IconButton(
                    onClick = {
                        bandValues.indices.forEach { bandValues[it] = 0f }
                        selectedPreset = "عادي"
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.reset_normal),
                        tint = BatPink
                    )
                }
            }

            // بطاقة النطاقات
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bands.forEachIndexed { index, band ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // اسم النطاق
                            Text(
                                text = band,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(56.dp),
                                color = bandColors[index],
                                fontWeight = FontWeight.Bold
                            )
                            // شريط التمرير
                            Slider(
                                value = bandValues[index],
                                onValueChange = {
                                    bandValues[index] = it
                                    selectedPreset = "مخصص"
                                },
                                valueRange = -10f..10f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = bandColors[index],
                                    activeTrackColor = bandColors[index],
                                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )
                            // قيمة dB
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bandColors[index].copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${bandValues[index].toInt()}dB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = bandColors[index],
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // زر إعادة التعيين الكامل
            OutlinedButton(
                onClick = {
                    bandValues.indices.forEach { bandValues[it] = 0f }
                    selectedPreset = "عادي"
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BatPurple
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.reset_normal), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun applyPreset(preset: String, bands: MutableList<Float>) {
    val values = when (preset) {
        "بوب" -> listOf(-1f, 2f, 4f, 3f, 1f)
        "روك" -> listOf(4f, 2f, -1f, 2f, 4f)
        "جاز" -> listOf(3f, 2f, 0f, 2f, 3f)
        "كلاسيك" -> listOf(4f, 3f, -2f, 3f, 4f)
        "هيب هوب" -> listOf(5f, 3f, 0f, -1f, 2f)
        "إلكترونيك" -> listOf(4f, 2f, 0f, 2f, 4f)
        "باس" -> listOf(6f, 4f, 0f, -2f, -2f)
        "ترِبل" -> listOf(-2f, -2f, 0f, 4f, 6f)
        "صوتي" -> listOf(-2f, 0f, 4f, 3f, -1f)
        else -> listOf(0f, 0f, 0f, 0f, 0f)
    }
    values.forEachIndexed { i, v -> bands[i] = v }
}

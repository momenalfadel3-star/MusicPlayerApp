package com.alkhufash.music.presentation.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkhufash.music.R
import com.alkhufash.music.presentation.theme.BatOrange
import com.alkhufash.music.presentation.theme.BatPink
import com.alkhufash.music.presentation.theme.BatPurple
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
                title = {
                    Text(
                        stringResource(R.string.timer),
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // بطاقة مؤقت النوم (إيقاف التشغيل)
            TimerCard(
                title = stringResource(R.string.sleep_timer),
                subtitle = stringResource(R.string.sleep_timer_subtitle),
                icon = Icons.Default.NightShelter,
                gradientColors = listOf(BatPurple, BatPink),
                isActive = isSleepTimerActive,
                activeMinutes = sleepMinutes,
                sliderValue = sleepTimerValue,
                onSliderChange = { sleepTimerValue = it },
                onSetTimer = { viewModel.setSleepTimer(sleepTimerValue.toInt()) },
                onCancelTimer = { viewModel.cancelSleepTimer() }
            )

            // بطاقة مؤقت البدء (تشغيل مؤجل)
            TimerCard(
                title = stringResource(R.string.start_timer),
                subtitle = stringResource(R.string.start_timer_subtitle),
                icon = Icons.Default.PlayCircle,
                gradientColors = listOf(BatOrange, BatPink),
                isActive = isStartTimerActive,
                activeMinutes = startMinutes,
                sliderValue = startTimerValue,
                onSliderChange = { startTimerValue = it },
                onSetTimer = { viewModel.setStartTimer(startTimerValue.toInt()) },
                onCancelTimer = { viewModel.cancelStartTimer() }
            )

            // اختيارات سريعة
            Text(
                stringResource(R.string.quick_presets),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 45, 60).forEach { minutes ->
                    FilterChip(
                        selected = sleepTimerValue == minutes.toFloat(),
                        onClick = { sleepTimerValue = minutes.toFloat() },
                        label = { Text("${minutes}د") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BatPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // ملاحظة توضيحية
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BatPurple.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = BatPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "مؤقت النوم يوقف التشغيل تلقائياً، ومؤقت البدء يشغل الموسيقى بعد المدة المحددة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // رأس البطاقة
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(gradientColors)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isActive) {
                    // مؤشر نشط متوهج
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(gradientColors.first())
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "${activeMinutes}د",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isActive) {
                // عرض الوقت المختار
                Text(
                    "${sliderValue.toInt()} دقيقة",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = gradientColors.first()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = onSliderChange,
                    valueRange = 5f..120f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = gradientColors.first(),
                        activeTrackColor = gradientColors.first(),
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "5 دقائق",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "ساعتان",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // زر الضبط
                Button(
                    onClick = onSetTimer,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gradientColors.first()
                    )
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ضبط المؤقت",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // حالة نشطة
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = gradientColors.first(),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "المؤقت نشط: $activeMinutes دقيقة متبقية",
                        style = MaterialTheme.typography.bodyMedium,
                        color = gradientColors.first(),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onCancelTimer,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = gradientColors.first()
                    )
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إلغاء المؤقت", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

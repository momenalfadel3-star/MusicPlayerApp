package com.alkhufash.music.presentation.screens.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkhufash.music.R
import com.alkhufash.music.presentation.theme.BatCyan
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
    // العداد التنازلي الحقيقي
    val sleepTimerDisplay by viewModel.sleepTimerDisplay.collectAsStateWithLifecycle()
    val startTimerDisplay by viewModel.startTimerDisplay.collectAsStateWithLifecycle()

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
                countdownDisplay = sleepTimerDisplay,
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
                countdownDisplay = startTimerDisplay,
                sliderValue = startTimerValue,
                onSliderChange = { startTimerValue = it },
                onSetTimer = { viewModel.startTimerWithService(startTimerValue.toInt()) },
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
    countdownDisplay: String,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    onSetTimer: () -> Unit,
    onCancelTimer: () -> Unit
) {
    // تأثير نبضة للمؤقت النشط
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

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
                            "نشط",
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
                // ===== عرض العداد التنازلي الحقيقي =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    gradientColors.first().copy(alpha = 0.15f),
                                    gradientColors.last().copy(alpha = 0.15f)
                                )
                            )
                        )
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "الوقت المتبقي",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // العداد التنازلي الكبير
                        Text(
                            text = countdownDisplay,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = gradientColors.first(),
                            modifier = Modifier.scale(pulseScale)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$activeMinutes دقيقة متبقية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // مؤشر نشاط
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // نقطة خضراء نابضة
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(BatCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "المؤقت يعمل...",
                        style = MaterialTheme.typography.bodySmall,
                        color = BatCyan,
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

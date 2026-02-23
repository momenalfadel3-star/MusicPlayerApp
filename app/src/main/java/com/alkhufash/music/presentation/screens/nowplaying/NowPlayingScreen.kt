package com.alkhufash.music.presentation.screens.nowplaying

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.alkhufash.music.R
import com.alkhufash.music.presentation.theme.BatOrange
import com.alkhufash.music.presentation.theme.BatPink
import com.alkhufash.music.presentation.theme.BatPurple
import com.alkhufash.music.presentation.theme.BatPurpleLight
import com.alkhufash.music.presentation.viewmodel.MusicViewModel
import com.alkhufash.music.service.PlayerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: MusicViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToEqualizer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()

    val song = uiState.currentSong

    // انيميشن دوران الأسطوانة
    val rotation = rememberInfiniteTransition(label = "rotation")
    val rotationAngle by rotation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // انيميشن نبض لزر التشغيل
    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BatPurple.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // شريط العنوان العلوي
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.cancel),
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.now_playing_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
                    )
                }
                IconButton(onClick = { /* Queue */ }) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = stringResource(R.string.queue),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // الأسطوانة الدوارة - تصميم عصري
            Box(
                modifier = Modifier
                    .size(270.dp),
                contentAlignment = Alignment.Center
            ) {
                // حلقة خارجية متوهجة
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BatPurple.copy(alpha = 0.4f),
                                    BatPink.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // الأسطوانة الرئيسية
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .rotate(if (playerState.isPlaying) rotationAngle else rotationAngle),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = song?.let { "content://media/external/audio/albumart/${it.albumId}" },
                        contentDescription = stringResource(R.string.albums),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // تدرج فوق الصورة
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.2f)
                                    )
                                )
                            )
                    )
                    // الثقب المركزي
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                    )
                    // نقطة مركزية
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BatPurple)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // معلومات الأغنية
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = song?.title ?: stringResource(R.string.no_song_playing),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = song?.artist ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = BatPurpleLight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = song?.album ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // شريط التقدم
            Column(
                modifier = Modifier.padding(horizontal = 28.dp)
            ) {
                Slider(
                    value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                    onValueChange = { progress ->
                        viewModel.seekTo((progress * duration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = BatPink,
                        activeTrackColor = BatPurple,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(currentPosition),
                        style = MaterialTheme.typography.labelSmall,
                        color = BatPurpleLight
                    )
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // أزرار التحكم الرئيسية
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // تشغيل عشوائي
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = stringResource(R.string.shuffle),
                        tint = if (playerState.isShuffleOn) BatPink
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // السابق
                IconButton(
                    onClick = { viewModel.seekToPrevious() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "السابق",
                        modifier = Modifier.size(38.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // تشغيل/إيقاف - زر كبير متوهج
                Box(
                    modifier = Modifier
                        .size(if (playerState.isPlaying) (72 * pulseScale).dp else 72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(BatPurple, BatPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.playOrPause() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) stringResource(R.string.sleep_timer) else stringResource(R.string.start_timer),
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }

                // التالي
                IconButton(
                    onClick = { viewModel.seekToNext() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "التالي",
                        modifier = Modifier.size(38.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // تكرار
                IconButton(onClick = { viewModel.toggleRepeat() }) {
                    Icon(
                        imageVector = when (playerState.repeatMode) {
                            Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = stringResource(R.string.repeat),
                        tint = if (playerState.repeatMode != Player.REPEAT_MODE_OFF) BatPink
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // أزرار إضافية
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // مفضلة
                    IconButton(onClick = { song?.let { viewModel.toggleFavorite(it.id) } }) {
                        Icon(
                            imageVector = if (song?.isFavorite == true) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorites),
                            tint = if (song?.isFavorite == true) BatPink
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // مؤقت
                    IconButton(onClick = onNavigateToTimer) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = stringResource(R.string.timer),
                            tint = BatOrange
                        )
                    }

                    // معادل صوتي
                    IconButton(onClick = onNavigateToEqualizer) {
                        Icon(
                            Icons.Default.Equalizer,
                            contentDescription = stringResource(R.string.equalizer),
                            tint = BatPurpleLight
                        )
                    }

                    // مشاركة
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val seconds = (ms / 1000) % 60
    val minutes = (ms / 1000) / 60
    return "%d:%02d".format(minutes, seconds)
}

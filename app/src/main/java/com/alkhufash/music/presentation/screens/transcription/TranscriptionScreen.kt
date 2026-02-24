package com.alkhufash.music.presentation.screens.transcription

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alkhufash.music.audio.SpeechToTextManager
import com.alkhufash.music.audio.TranscriptionRecord
import com.alkhufash.music.presentation.theme.BatCyan
import com.alkhufash.music.presentation.theme.BatOrange
import com.alkhufash.music.presentation.theme.BatPink
import com.alkhufash.music.presentation.theme.BatPurple
import com.alkhufash.music.presentation.theme.BatPurpleLight
import com.alkhufash.music.presentation.viewmodel.MusicViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TranscriptionScreen(
    viewModel: MusicViewModel,
    onNavigateBack: () -> Unit
) {
    val speechManager = viewModel.speechToTextManager
    val recognitionState by speechManager.recognitionState.collectAsStateWithLifecycle()
    val isListening by speechManager.isListening.collectAsStateWithLifecycle()
    val transcriptions by speechManager.transcriptions.collectAsStateWithLifecycle()

    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val clipboardManager = LocalClipboardManager.current

    var selectedLanguage by remember { mutableStateOf("ar-SA") }
    var currentText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    // تحديث النص الحالي من حالة التعرف
    LaunchedEffect(recognitionState) {
        when (val state = recognitionState) {
            is SpeechToTextManager.RecognitionState.PartialResult -> {
                currentText = state.text
            }
            is SpeechToTextManager.RecognitionState.FinalResult -> {
                currentText = state.text
                // حفظ تلقائي عند الانتهاء
                if (state.text.isNotBlank()) {
                    speechManager.saveTranscription(state.text, selectedLanguage)
                }
            }
            else -> {}
        }
    }

    // انيميشن نبض زر الميكروفون
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    // حوار تأكيد الحذف
    showDeleteDialog?.let { id ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("حذف السجل") },
            text = { Text("هل تريد حذف هذا السجل؟") },
            confirmButton = {
                TextButton(onClick = {
                    speechManager.deleteTranscription(id)
                    showDeleteDialog = null
                }) { Text("حذف", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("إلغاء") }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("مسح جميع السجلات") },
            text = { Text("هل تريد مسح جميع سجلات التحويل؟") },
            confirmButton = {
                TextButton(onClick = {
                    speechManager.clearAllTranscriptions()
                    showClearDialog = false
                }) { Text("مسح الكل", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("إلغاء") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "تحويل الصوت إلى نص",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "رجوع")
                    }
                },
                actions = {
                    if (transcriptions.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                "مسح الكل",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بطاقة التسجيل الرئيسية
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // اختيار اللغة
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("ar-SA" to "عربي", "en-US" to "English").forEach { (code, label) ->
                            FilterChip(
                                selected = selectedLanguage == code,
                                onClick = { if (!isListening) selectedLanguage = code },
                                label = { Text(label, fontWeight = FontWeight.Medium) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BatPurple,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // منطقة عرض النص
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = if (isListening) 2.dp else 1.dp,
                                color = if (isListening) BatCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = if (currentText.isEmpty()) Alignment.Center else Alignment.TopStart
                    ) {
                        if (currentText.isEmpty() && !isListening) {
                            Text(
                                "اضغط على زر الميكروفون للبدء...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = currentText.ifEmpty { "جارٍ الاستماع..." },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (currentText.isEmpty()) BatCyan
                                else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 28.sp
                            )
                        }
                    }

                    // حالة التعرف
                    AnimatedVisibility(
                        visible = recognitionState is SpeechToTextManager.RecognitionState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val error = recognitionState as? SpeechToTextManager.RecognitionState.Error
                        Text(
                            text = error?.message ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // أزرار التحكم
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // زر الميكروفون الرئيسي
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(if (isListening) micScale else 1f)
                                .clip(CircleShape)
                                .background(
                                    if (isListening)
                                        Brush.radialGradient(listOf(BatPink, BatPurple))
                                    else
                                        Brush.radialGradient(listOf(BatPurple, BatPurpleLight))
                                )
                                .clickable {
                                    if (!micPermission.status.isGranted) {
                                        micPermission.launchPermissionRequest()
                                    } else if (isListening) {
                                        speechManager.stopListening()
                                    } else {
                                        currentText = ""
                                        speechManager.startListening(selectedLanguage)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = if (isListening) "إيقاف" else "بدء التسجيل",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    !micPermission.status.isGranted -> "يتطلب إذن الميكروفون"
                                    isListening -> "جارٍ الاستماع..."
                                    recognitionState is SpeechToTextManager.RecognitionState.FinalResult -> "تم التحويل!"
                                    else -> "اضغط للتسجيل"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isListening) BatCyan else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (selectedLanguage == "ar-SA") "اللغة: العربية" else "Language: English",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // زر نسخ النص
                        if (currentText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(currentText))
                                }
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    "نسخ",
                                    tint = BatOrange
                                )
                            }
                        }
                    }

                    // تحذير إذا لم تكن الخدمة متوفرة
                    if (!speechManager.isAvailable()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "خدمة التعرف على الكلام غير متوفرة. تأكد من تثبيت Google Speech Services.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // قائمة السجلات
            if (transcriptions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "السجلات المحفوظة (${transcriptions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transcriptions, key = { it.id }) { record ->
                        TranscriptionRecordCard(
                            record = record,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(record.text))
                            },
                            onDelete = { showDeleteDialog = record.id }
                        )
                    }
                }
            } else if (!isListening) {
                // رسالة فارغة
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.RecordVoiceOver,
                        null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        "لا توجد سجلات بعد",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        "ابدأ التسجيل لتحويل صوتك إلى نص",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TranscriptionRecordCard(
    record: TranscriptionRecord,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // رأس البطاقة
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.TextSnippet,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = BatPurple
                    )
                    Text(
                        record.getLanguageDisplay(),
                        style = MaterialTheme.typography.labelSmall,
                        color = BatPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (record.songTitle != null) {
                        Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            record.songTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = BatOrange,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 120.dp)
                        )
                    }
                }
                Text(
                    record.getFormattedTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // النص المحول
            Text(
                text = record.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            // أزرار الإجراءات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onCopy,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = BatCyan
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("نسخ", style = MaterialTheme.typography.labelMedium, color = BatCyan)
                }
                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "حذف",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

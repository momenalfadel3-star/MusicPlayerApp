package com.alkhufash.music.utils

object DurationUtils {
    
    /**
     * دالة تحويل المدة الطويلة لنص مناسب
     * تدعم المدد التي تزيد عن 5 ساعات بشكل صحيح
     */
    fun formatDuration(millis: Long): String {
        if (millis <= 0) return "00:00"
        
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return when {
            hours > 0 -> {
                // لو المدة ساعات (أكثر من 60 دقيقة)
                if (hours >= 100) {
                    // لو المدة 100 ساعة أو أكثر
                    String.format("%d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }
            }
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
            else -> String.format("00:%02d", seconds)
        }
    }
}

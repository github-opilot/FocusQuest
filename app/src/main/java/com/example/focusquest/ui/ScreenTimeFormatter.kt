package com.example.focusquest.ui

internal fun formatScreenTime(milliseconds: Long): String {
    val totalSeconds = milliseconds.coerceAtLeast(0L) / 1_000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L

    return when {
        seconds == 0L -> "$minutes min"
        minutes == 0L -> "$seconds sec"
        else -> "$minutes min $seconds sec"
    }
}


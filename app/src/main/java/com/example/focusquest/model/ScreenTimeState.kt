package com.example.focusquest.model

data class ScreenTimeState(
    val baseDailyMinutes: Int = 30,
    val earnedMinutes: Int = 0,
    val usedMinutes: Int = 0,
    val usedTimeMillis: Long = usedMinutes.toLong() * MILLIS_PER_MINUTE
) {
    val availableMinutes: Int
        get() = (baseDailyMinutes.toLong() + earnedMinutes.toLong() - usedMinutes.toLong())
            .coerceIn(0L, Int.MAX_VALUE.toLong())
            .toInt()

    companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}

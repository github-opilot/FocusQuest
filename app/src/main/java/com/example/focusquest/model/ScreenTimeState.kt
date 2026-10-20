package com.example.focusquest.model

data class ScreenTimeState(
    val baseDailyMinutes: Int = 30,
    val earnedMinutes: Int = 0,
    val usedMinutes: Int = 0,
    val usedTimeMillis: Long = usedMinutes.toLong() * MILLIS_PER_MINUTE,
    val earnedTimeMillis: Long = earnedMinutes.toLong() * MILLIS_PER_MINUTE
) {
    val availableTimeMillis: Long
        get() {
            val baseTimeMillis = baseDailyMinutes.coerceAtLeast(0).toLong() * MILLIS_PER_MINUTE
            val totalEarnedMillis = earnedTimeMillis.coerceAtLeast(0L)
            val totalAllowance = if (Long.MAX_VALUE - baseTimeMillis < totalEarnedMillis) {
                Long.MAX_VALUE
            } else {
                baseTimeMillis + totalEarnedMillis
            }
            return (totalAllowance - usedTimeMillis.coerceAtLeast(0L)).coerceAtLeast(0L)
        }

    val availableMinutes: Int
        get() = (availableTimeMillis / MILLIS_PER_MINUTE)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()

    companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}

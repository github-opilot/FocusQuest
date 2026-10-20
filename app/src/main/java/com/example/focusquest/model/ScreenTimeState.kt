package com.example.focusquest.model

data class ScreenTimeState(
    val baseDailyMinutes: Int = 30,
    val earnedMinutes: Int = 0,
    val usedMinutes: Int = 0
) {
    val availableMinutes: Int
        get() = (baseDailyMinutes + earnedMinutes - usedMinutes).coerceAtLeast(0)
}

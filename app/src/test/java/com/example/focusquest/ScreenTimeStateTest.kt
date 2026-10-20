package com.example.focusquest

import com.example.focusquest.model.ScreenTimeState
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTimeStateTest {
    @Test
    fun availableMinutesIncludesEarnedAndUsedMinutes() {
        val state = ScreenTimeState(
            baseDailyMinutes = 30,
            earnedMinutes = 10,
            usedMinutes = 5
        )

        assertEquals(35, state.availableMinutes)
    }

    @Test
    fun availableMinutesNeverBecomesNegative() {
        val state = ScreenTimeState(
            baseDailyMinutes = 30,
            earnedMinutes = 0,
            usedMinutes = 40
        )

        assertEquals(0, state.availableMinutes)
    }

    @Test
    fun quizRewardsIncreaseAvailableMinutes() {
        val state = ScreenTimeState(
            baseDailyMinutes = 30,
            earnedMinutes = 6,
            usedMinutes = 0
        )

        assertEquals(36, state.availableMinutes)
    }
}

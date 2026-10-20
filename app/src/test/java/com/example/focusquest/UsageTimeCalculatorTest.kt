package com.example.focusquest

import com.example.focusquest.data.UsageTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class UsageTimeCalculatorTest {
    private val minute = 60_000L

    @Test
    fun usageAfterBaselineCountsOnlyTheDifference() {
        assertEquals(5 * minute, UsageTimeCalculator.newlyObservedMillis(20 * minute, 25 * minute))
    }

    @Test
    fun repeatedSnapshotDoesNotCountTheSameUsageAgain() {
        val afterFirstRefresh = 25 * minute
        assertEquals(0L, UsageTimeCalculator.newlyObservedMillis(afterFirstRefresh, 25 * minute))
    }

    @Test
    fun usageFromSeveralSelectedAppsIsCumulative() {
        val packages = setOf("tiktok", "youtube", "instagram")
        val baseline = mapOf("tiktok" to 0L, "youtube" to 0L, "instagram" to 0L)
        val current = mapOf("tiktok" to 12 * minute, "youtube" to 8 * minute, "instagram" to 3 * minute)

        assertEquals(23 * minute, UsageTimeCalculator.totalNewlyObservedMillis(packages, baseline, current))
    }

    @Test
    fun incoherentLowerUsageReadingAddsNothing() {
        assertEquals(0L, UsageTimeCalculator.newlyObservedMillis(12 * minute, 8 * minute))
    }
}

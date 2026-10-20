package com.example.focusquest

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.focusquest.data.ScreenTimeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenTimeRepositoryInstrumentedTest {
    @Test
    fun valuesPersistAndResetAtTheStartOfANewDay() = runBlocking {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val firstDay = "test-${System.nanoTime()}"
        val repository = ScreenTimeRepository(testContext) { firstDay }

        repository.resetIfNewDay()
        repository.setBaseDailyMinutes(45)
        repository.addEarnedMinutes(6)
        repository.addUsedMinutes(5)

        val savedState = repository.screenTimeState.first()
        assertEquals(45, savedState.baseDailyMinutes)
        assertEquals(6, savedState.earnedMinutes)
        assertEquals(5, savedState.usedMinutes)
        assertEquals(5 * 60_000L, savedState.usedTimeMillis)
        assertEquals(46, savedState.availableMinutes)

        val recreatedRepository = ScreenTimeRepository(testContext) { firstDay }
        assertEquals(savedState, recreatedRepository.screenTimeState.first())

        val nextDayRepository = ScreenTimeRepository(testContext) { "$firstDay-next" }
        nextDayRepository.resetIfNewDay()

        val resetState = nextDayRepository.screenTimeState.first()
        assertEquals(45, resetState.baseDailyMinutes)
        assertEquals(0, resetState.earnedMinutes)
        assertEquals(0, resetState.usedMinutes)
        assertEquals(45, resetState.availableMinutes)
        assertEquals(0L, resetState.usedTimeMillis)
    }

    @Test
    fun selectionBaselinesPersistAndSnapshotsOnlyAddNewUsage() = runBlocking {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val today = "selection-${System.nanoTime()}"
        val repository = ScreenTimeRepository(testContext) { today }
        val minute = 60_000L

        repository.resetIfNewDay()
        repository.setSelectedPackages(
            packages = setOf("com.example.youtube"),
            currentUsageMillis = mapOf("com.example.youtube" to 20 * minute)
        )
        assertEquals(setOf("com.example.youtube"), repository.selectedPackages.first())

        val recreatedRepository = ScreenTimeRepository(testContext) { today }
        assertEquals(setOf("com.example.youtube"), recreatedRepository.selectedPackages.first())

        recreatedRepository.addUsageSnapshot(mapOf("com.example.youtube" to 25 * minute))
        recreatedRepository.addUsageSnapshot(mapOf("com.example.youtube" to 25 * minute))
        assertEquals(5 * minute, recreatedRepository.screenTimeState.first().usedTimeMillis)
        assertEquals(5, recreatedRepository.screenTimeState.first().usedMinutes)

        recreatedRepository.setSelectedPackages(
            packages = setOf("com.example.youtube", "com.example.tiktok"),
            currentUsageMillis = mapOf(
                "com.example.youtube" to 25 * minute,
                "com.example.tiktok" to 12 * minute
            )
        )
        recreatedRepository.addUsageSnapshot(
            mapOf("com.example.youtube" to 28 * minute, "com.example.tiktok" to 16 * minute)
        )
        assertEquals(12 * minute, recreatedRepository.screenTimeState.first().usedTimeMillis)

        recreatedRepository.setSelectedPackages(
            packages = setOf("com.example.tiktok"),
            currentUsageMillis = mapOf("com.example.tiktok" to 16 * minute)
        )
        assertTrue("com.example.youtube" !in recreatedRepository.selectedPackages.first())
        recreatedRepository.addUsageSnapshot(mapOf("com.example.tiktok" to 17 * minute))
        assertEquals(13 * minute, recreatedRepository.screenTimeState.first().usedTimeMillis)

        val nextDayRepository = ScreenTimeRepository(testContext) { "$today-next" }
        nextDayRepository.resetIfNewDay()
        assertEquals(0L, nextDayRepository.screenTimeState.first().usedTimeMillis)
        nextDayRepository.addUsageSnapshot(mapOf("com.example.tiktok" to 2 * minute))
        assertEquals(2 * minute, nextDayRepository.screenTimeState.first().usedTimeMillis)
    }
}

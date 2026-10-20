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
        val testContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataStore = isolatedPreferencesDataStore(testContext, "screen-time-values")
        val firstDay = "test-${System.nanoTime()}"
        val repository = ScreenTimeRepository(testContext, dataStoreOverride = dataStore) { firstDay }

        repository.resetIfNewDay()
        repository.setBaseDailyMinutes(45)
        repository.addEarnedMinutes(6)
        repository.addEarnedTimeMillis(2 * 60_000L)
        repository.addUsedMinutes(5)
        repository.setAppProtectionEnabled(true)

        val savedState = repository.screenTimeState.first()
        assertEquals(45, savedState.baseDailyMinutes)
        assertEquals(8, savedState.earnedMinutes)
        assertEquals(8 * 60_000L, savedState.earnedTimeMillis)
        assertEquals(5, savedState.usedMinutes)
        assertEquals(5 * 60_000L, savedState.usedTimeMillis)
        assertEquals(48, savedState.availableMinutes)
        assertTrue(repository.appProtectionEnabled.first())

        val recreatedRepository = ScreenTimeRepository(testContext, dataStoreOverride = dataStore) { firstDay }
        assertEquals(savedState, recreatedRepository.screenTimeState.first())
        assertTrue(recreatedRepository.appProtectionEnabled.first())

        val nextDayRepository = ScreenTimeRepository(testContext, dataStoreOverride = dataStore) { "$firstDay-next" }
        nextDayRepository.resetIfNewDay()

        val resetState = nextDayRepository.screenTimeState.first()
        assertEquals(45, resetState.baseDailyMinutes)
        assertEquals(0, resetState.earnedMinutes)
        assertEquals(0, resetState.usedMinutes)
        assertEquals(45, resetState.availableMinutes)
        assertEquals(0L, resetState.usedTimeMillis)
        assertTrue(nextDayRepository.appProtectionEnabled.first())
    }

    @Test
    fun selectionBaselinesPersistAndSnapshotsOnlyAddNewUsage() = runBlocking {
        val testContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataStore = isolatedPreferencesDataStore(testContext, "screen-time-app-selection")
        val today = "selection-${System.nanoTime()}"
        val repository = ScreenTimeRepository(testContext, dataStoreOverride = dataStore) { today }
        val minute = 60_000L

        repository.resetIfNewDay()
        repository.setSelectedPackages(emptySet())
        repository.setSelectedPackages(
            packages = setOf("com.example.youtube"),
            currentUsageMillis = mapOf("com.example.youtube" to 20 * minute)
        )
        assertEquals(setOf("com.example.youtube"), repository.selectedPackages.first())

        val recreatedRepository = ScreenTimeRepository(testContext, dataStoreOverride = dataStore) { today }
        assertEquals(setOf("com.example.youtube"), recreatedRepository.selectedPackages.first())

        recreatedRepository.addUsageSnapshot(mapOf("com.example.youtube" to 25 * minute))
        recreatedRepository.addUsageSnapshot(mapOf("com.example.youtube" to 25 * minute))
        assertEquals(25 * minute, recreatedRepository.screenTimeState.first().usedTimeMillis)
        assertEquals(25, recreatedRepository.screenTimeState.first().usedMinutes)

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
        assertEquals(44 * minute, recreatedRepository.screenTimeState.first().usedTimeMillis)

        recreatedRepository.setSelectedPackages(
            packages = setOf("com.example.tiktok"),
            currentUsageMillis = mapOf("com.example.tiktok" to 16 * minute)
        )
        assertTrue("com.example.youtube" !in recreatedRepository.selectedPackages.first())
        recreatedRepository.addUsageSnapshot(mapOf("com.example.tiktok" to 17 * minute))
        assertEquals(45 * minute, recreatedRepository.screenTimeState.first().usedTimeMillis)

        val nextDayRepository = ScreenTimeRepository(testContext, dataStoreOverride = dataStore) { "$today-next" }
        nextDayRepository.resetIfNewDay()
        assertEquals(0L, nextDayRepository.screenTimeState.first().usedTimeMillis)
        nextDayRepository.addUsageSnapshot(mapOf("com.example.tiktok" to 2 * minute))
        assertEquals(2 * minute, nextDayRepository.screenTimeState.first().usedTimeMillis)
    }
}

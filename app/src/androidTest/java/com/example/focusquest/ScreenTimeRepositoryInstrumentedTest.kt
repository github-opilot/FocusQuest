package com.example.focusquest

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.focusquest.data.ScreenTimeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
    }
}

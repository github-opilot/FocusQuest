package com.example.focusquest

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.focusquest.data.OnboardingRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingRepositoryInstrumentedTest {
    @Test
    fun completionFlagIsSavedAndReadBack() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dataStore = isolatedPreferencesDataStore(context, "onboarding")
        val repository = OnboardingRepository(context, dataStore)
        try {
            repository.setHasSeenOnboarding(false)
            assertFalse(repository.hasSeenOnboarding.first())

            repository.setHasSeenOnboarding(true)
            val reopenedRepository = OnboardingRepository(context, dataStore)
            assertTrue(reopenedRepository.hasSeenOnboarding.first())
        } finally {
            repository.setHasSeenOnboarding(false)
        }
    }
}

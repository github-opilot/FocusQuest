package com.example.focusquest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.focusquest.model.ScreenTimeState
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.screenTimeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "screen_time_wallet"
)

class ScreenTimeRepository(
    context: Context,
    private val currentDate: () -> String = ::getCurrentDate
) {
    private val dataStore = context.applicationContext.screenTimeDataStore

    val screenTimeState: Flow<ScreenTimeState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ScreenTimeState(
                baseDailyMinutes = preferences[BASE_DAILY_MINUTES] ?: DEFAULT_BASE_MINUTES,
                earnedMinutes = preferences[EARNED_MINUTES] ?: 0,
                usedMinutes = preferences[USED_MINUTES] ?: 0
            )
        }

    suspend fun setBaseDailyMinutes(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[BASE_DAILY_MINUTES] = minutes.coerceAtLeast(0)
        }
    }

    suspend fun addEarnedMinutes(minutes: Int) {
        if (minutes <= 0) return

        dataStore.edit { preferences ->
            val currentMinutes = preferences[EARNED_MINUTES] ?: 0
            preferences[EARNED_MINUTES] = currentMinutes + minutes
        }
    }

    suspend fun addUsedMinutes(minutes: Int) {
        if (minutes <= 0) return

        dataStore.edit { preferences ->
            val currentMinutes = preferences[USED_MINUTES] ?: 0
            preferences[USED_MINUTES] = currentMinutes + minutes
        }
    }

    suspend fun resetIfNewDay() {
        val today = currentDate()

        dataStore.edit { preferences ->
            if (preferences[LAST_RESET_DATE] != today) {
                preferences[EARNED_MINUTES] = 0
                preferences[USED_MINUTES] = 0
                preferences[LAST_RESET_DATE] = today
            }
        }
    }

    private companion object {
        const val DEFAULT_BASE_MINUTES = 30

        val BASE_DAILY_MINUTES = intPreferencesKey("base_daily_minutes")
        val EARNED_MINUTES = intPreferencesKey("earned_minutes")
        val USED_MINUTES = intPreferencesKey("used_minutes")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
    }
}

private fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

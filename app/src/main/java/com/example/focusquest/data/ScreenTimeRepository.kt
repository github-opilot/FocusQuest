package com.example.focusquest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
            val earnedTimeMillis = preferences[EARNED_TIME_MILLIS]
                ?: (preferences[EARNED_MINUTES] ?: 0).toLong() * ScreenTimeState.MILLIS_PER_MINUTE
            val usedTimeMillis = preferences[USED_TIME_MILLIS]
                ?: (preferences[USED_MINUTES] ?: 0).toLong() * ScreenTimeState.MILLIS_PER_MINUTE
            ScreenTimeState(
                baseDailyMinutes = preferences[BASE_DAILY_MINUTES] ?: DEFAULT_BASE_MINUTES,
                earnedMinutes = earnedMinutesFrom(earnedTimeMillis),
                usedMinutes = usedMinutesFrom(usedTimeMillis),
                usedTimeMillis = usedTimeMillis,
                earnedTimeMillis = earnedTimeMillis
            )
        }

    val selectedPackages: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[SELECTED_PACKAGES] ?: emptySet() }

    suspend fun setBaseDailyMinutes(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[BASE_DAILY_MINUTES] = minutes.coerceAtLeast(0)
        }
    }

    suspend fun addEarnedMinutes(minutes: Int) {
        if (minutes <= 0) return
        addEarnedTimeMillis(minutes.toLong() * ScreenTimeState.MILLIS_PER_MINUTE)
    }

    suspend fun addEarnedTimeMillis(milliseconds: Long) {
        if (milliseconds <= 0L) return

        dataStore.edit { preferences ->
            val currentMillis = preferences[EARNED_TIME_MILLIS]
                ?: (preferences[EARNED_MINUTES] ?: 0).toLong() * ScreenTimeState.MILLIS_PER_MINUTE
            val updatedMillis = saturatingAdd(currentMillis.coerceAtLeast(0L), milliseconds)
            preferences[EARNED_TIME_MILLIS] = updatedMillis
            preferences[EARNED_MINUTES] = earnedMinutesFrom(updatedMillis)
        }
    }

    suspend fun addUsedMinutes(minutes: Int) {
        if (minutes <= 0) return

        dataStore.edit { preferences ->
            val currentMillis = preferences[USED_TIME_MILLIS]
                ?: (preferences[USED_MINUTES] ?: 0).toLong() * ScreenTimeState.MILLIS_PER_MINUTE
            val updatedMillis = (currentMillis + minutes.toLong() * ScreenTimeState.MILLIS_PER_MINUTE)
                .coerceAtLeast(currentMillis)
            preferences[USED_TIME_MILLIS] = updatedMillis
            preferences[USED_MINUTES] = usedMinutesFrom(updatedMillis)
        }
    }

    /**
     * Saves the app selection and records a fresh baseline for newly selected packages.
     * A missing reading is kept as an unknown baseline and initialized on the first
     * successful usage-stat refresh, so time before access was granted is not charged.
     */
    suspend fun setSelectedPackages(
        packages: Set<String>,
        currentUsageMillis: Map<String, Long>? = null
    ) {
        val normalizedPackages = packages.filter(String::isNotBlank).toSet()
        val today = currentDate()

        dataStore.edit { preferences ->
            resetForDateIfNeeded(preferences, today)
            val previousPackages = preferences[SELECTED_PACKAGES] ?: emptySet()
            (previousPackages - normalizedPackages).forEach { packageName ->
                preferences.remove(baselineKey(packageName))
                preferences.remove(lastReadingKey(packageName))
            }
            (normalizedPackages - previousPackages).forEach { packageName ->
                val reading = currentUsageMillis?.get(packageName)?.coerceAtLeast(0L) ?: UNKNOWN_READING
                preferences[baselineKey(packageName)] = reading
                preferences[lastReadingKey(packageName)] = reading
            }
            preferences[SELECTED_PACKAGES] = normalizedPackages
        }
    }

    /** Adds only newly observed foreground time; repeated snapshots cannot double count. */
    suspend fun addUsageSnapshot(currentUsageMillis: Map<String, Long>) {
        val today = currentDate()

        dataStore.edit { preferences ->
            resetForDateIfNeeded(preferences, today)
            val packages = preferences[SELECTED_PACKAGES] ?: emptySet()
            var addedMillis = 0L

            packages.forEach { packageName ->
                val currentReading = currentUsageMillis[packageName]?.coerceAtLeast(0L) ?: return@forEach
                val baselineKey = baselineKey(packageName)
                val lastKey = lastReadingKey(packageName)
                val baseline = preferences[baselineKey] ?: UNKNOWN_READING

                if (baseline < 0L) {
                    // The app was selected before usage access was available.
                    preferences[baselineKey] = currentReading
                    preferences[lastKey] = currentReading
                    return@forEach
                }

                val previousReading = (preferences[lastKey] ?: baseline).coerceAtLeast(0L)
                val newlyObservedMillis = UsageTimeCalculator.newlyObservedMillis(previousReading, currentReading)
                if (newlyObservedMillis > 0L) {
                    addedMillis = saturatingAdd(addedMillis, newlyObservedMillis)
                    preferences[lastKey] = currentReading
                }
            }

            if (addedMillis > 0L) {
                val previousUsedMillis = preferences[USED_TIME_MILLIS]
                    ?: (preferences[USED_MINUTES] ?: 0).toLong() * ScreenTimeState.MILLIS_PER_MINUTE
                val totalUsedMillis = saturatingAdd(previousUsedMillis, addedMillis)
                preferences[USED_TIME_MILLIS] = totalUsedMillis
                preferences[USED_MINUTES] = usedMinutesFrom(totalUsedMillis)
            }
        }
    }

    /** Removes selected apps that are no longer installed and discards their snapshots. */
    suspend fun removeUninstalledPackages(installedPackages: Set<String>) {
        val today = currentDate()
        dataStore.edit { preferences ->
            resetForDateIfNeeded(preferences, today)
            val selected = preferences[SELECTED_PACKAGES] ?: emptySet()
            val kept = selected intersect installedPackages
            (selected - kept).forEach { packageName ->
                preferences.remove(baselineKey(packageName))
                preferences.remove(lastReadingKey(packageName))
            }
            preferences[SELECTED_PACKAGES] = kept
        }
    }

    suspend fun resetIfNewDay() {
        val today = currentDate()

        dataStore.edit { preferences ->
            resetForDateIfNeeded(preferences, today)
        }
    }

    private fun resetForDateIfNeeded(preferences: androidx.datastore.preferences.core.MutablePreferences, today: String) {
        if (preferences[LAST_RESET_DATE] == today) return

        preferences[EARNED_MINUTES] = 0
        preferences[EARNED_TIME_MILLIS] = 0L
        preferences[USED_MINUTES] = 0
        preferences[USED_TIME_MILLIS] = 0L
        preferences[LAST_RESET_DATE] = today

        val selected = preferences[SELECTED_PACKAGES] ?: emptySet()
        selected.forEach { packageName ->
            preferences[baselineKey(packageName)] = 0L
            preferences[lastReadingKey(packageName)] = 0L
        }
    }

    private fun baselineKey(packageName: String) = longPreferencesKey("$BASELINE_PREFIX$packageName")
    private fun lastReadingKey(packageName: String) = longPreferencesKey("$LAST_READING_PREFIX$packageName")

    private companion object {
        const val DEFAULT_BASE_MINUTES = 30
        const val BASELINE_PREFIX = "usage_baseline_"
        const val LAST_READING_PREFIX = "usage_last_reading_"
        const val UNKNOWN_READING = -1L

        val BASE_DAILY_MINUTES = intPreferencesKey("base_daily_minutes")
        val EARNED_MINUTES = intPreferencesKey("earned_minutes")
        val EARNED_TIME_MILLIS = longPreferencesKey("earned_time_millis")
        val USED_MINUTES = intPreferencesKey("used_minutes")
        val USED_TIME_MILLIS = longPreferencesKey("used_time_millis")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val SELECTED_PACKAGES = stringSetPreferencesKey("selected_packages")

        fun usedMinutesFrom(milliseconds: Long): Int =
            (milliseconds.coerceAtLeast(0L) / ScreenTimeState.MILLIS_PER_MINUTE)
                .coerceAtMost(Int.MAX_VALUE.toLong())
                .toInt()

        fun earnedMinutesFrom(milliseconds: Long): Int = usedMinutesFrom(milliseconds)

        fun saturatingAdd(first: Long, second: Long): Long =
            if (Long.MAX_VALUE - first < second) Long.MAX_VALUE else first + second
    }
}

private fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

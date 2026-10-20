package com.example.focusquest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusquest.data.ScreenTimeRepository
import com.example.focusquest.data.LaunchableApp
import com.example.focusquest.data.UsageStatsRepository
import com.example.focusquest.model.ScreenTimeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ScreenTimeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScreenTimeRepository(application)
    private val usageStatsRepository = UsageStatsRepository(application)
    private val refreshMutex = Mutex()

    private val _availableApps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    val availableApps: StateFlow<List<LaunchableApp>> = _availableApps

    private val _usageAccessGranted = MutableStateFlow(false)
    val usageAccessGranted: StateFlow<Boolean> = _usageAccessGranted

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val selectedPackages: StateFlow<Set<String>> = repository.selectedPackages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptySet()
    )

    val appProtectionEnabled: StateFlow<Boolean> = repository.appProtectionEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val screenTimeState: StateFlow<ScreenTimeState> = repository.screenTimeState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenTimeState()
    )

    init {
        refreshUsage()
    }

    fun addEarnedMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.addEarnedMinutes(minutes)
        }
    }

    fun addEarnedTimeMillis(milliseconds: Long) {
        viewModelScope.launch {
            repository.addEarnedTimeMillis(milliseconds)
        }
    }

    fun setBaseDailyMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.setBaseDailyMinutes(minutes)
        }
    }

    fun setAppProtectionEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAppProtectionEnabled(enabled)
        }
    }

    /** Refreshes app visibility, permission status, and today's usage snapshot. */
    fun refreshUsage() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshMutex.withLock {
                _isRefreshing.value = true
                try {
                    refreshUsageNow()
                } finally {
                    _isRefreshing.value = false
                }
            }
        }
    }

    /** Saves the user's selection with a baseline taken at the time of selection. */
    fun saveSelectedPackages(packages: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            refreshMutex.withLock {
                _isRefreshing.value = true
                try {
                    refreshUsageNow()
                    val accessGranted = usageStatsRepository.hasUsageAccess()
                    val baselineReadings = if (accessGranted) {
                        usageStatsRepository.getTodayForegroundTimeMillis(packages)
                    } else {
                        null
                    }
                    repository.setSelectedPackages(packages, baselineReadings)
                    _usageAccessGranted.value = accessGranted
                } finally {
                    _isRefreshing.value = false
                }
            }
        }
    }

    private suspend fun refreshUsageNow() {
        repository.resetIfNewDay()

        val apps = usageStatsRepository.getLaunchableApps()
        _availableApps.value = apps
        val accessGranted = usageStatsRepository.hasUsageAccess()
        _usageAccessGranted.value = accessGranted

        val selected = repository.selectedPackages.first()
        if (accessGranted && selected.isNotEmpty()) {
            usageStatsRepository.getTodayForegroundTimeMillis(selected)?.let { readings ->
                repository.addUsageSnapshot(readings)
            }
        }

        if (apps.isNotEmpty()) {
            repository.removeUninstalledPackages(apps.mapTo(mutableSetOf()) { it.packageName })
        }
    }
}

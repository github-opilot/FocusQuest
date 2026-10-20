package com.example.focusquest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusquest.data.ScreenTimeRepository
import com.example.focusquest.model.ScreenTimeState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScreenTimeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScreenTimeRepository(application)

    val screenTimeState: StateFlow<ScreenTimeState> = repository.screenTimeState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenTimeState()
    )

    init {
        viewModelScope.launch {
            repository.resetIfNewDay()
        }
    }

    fun addEarnedMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.addEarnedMinutes(minutes)
        }
    }

    fun setBaseDailyMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.setBaseDailyMinutes(minutes)
        }
    }
}

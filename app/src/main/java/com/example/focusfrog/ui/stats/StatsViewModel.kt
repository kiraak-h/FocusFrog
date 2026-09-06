package com.example.focusfrog.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusfrog.data.repository.FocusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsUiState(
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val todaySessions: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)

class StatsViewModel(
    private val focusRepository: FocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        // Observe user stats
        viewModelScope.launch {
            focusRepository.userStatsFlow.collectLatest { stats ->
                _uiState.update {
                    it.copy(
                        totalSessions = stats.totalSessions,
                        totalMinutes = stats.totalMinutes,
                        currentStreak = stats.currentStreak,
                        bestStreak = stats.bestStreak
                    )
                }
            }
        }

        // Observe today's sessions count
        viewModelScope.launch {
            focusRepository.getTodaySessionsCountFlow().collectLatest { count ->
                _uiState.update { it.copy(todaySessions = count) }
            }
        }
    }
}

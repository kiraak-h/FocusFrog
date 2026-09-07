package com.example.focusfrog.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusfrog.data.local.db.SessionEntity
import com.example.focusfrog.data.repository.FocusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DayGroup(
    val headerTitle: String,
    val sessionCount: Int,
    val totalMinutes: Int,
    val sessions: List<SessionEntity>
)

data class DiaryUiState(
    val dayGroups: List<DayGroup> = emptyList(),
    val isEmpty: Boolean = true
)

class DiaryViewModel(
    private val focusRepository: FocusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            focusRepository.recentSessionsFlow.collectLatest { sessions ->
                val groups = groupSessionsByDay(sessions)
                _uiState.update {
                    it.copy(
                        dayGroups = groups,
                        isEmpty = sessions.isEmpty()
                    )
                }
            }
        }
    }

    private fun groupSessionsByDay(sessions: List<SessionEntity>): List<DayGroup> {
        val today = LocalDate.now()
        val todayStr = today.toString()
        val yesterdayStr = today.minusDays(1).toString()

        val grouped = sessions.groupBy { it.completedDate }

        return grouped.map { (dateStr, daySessions) ->
            val headerTitle = when (dateStr) {
                todayStr -> "Today"
                yesterdayStr -> "Yesterday"
                else -> {
                    try {
                        val date = LocalDate.parse(dateStr)
                        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.US)
                        date.format(formatter)
                    } catch (_: Exception) {
                        dateStr
                    }
                }
            }

            val count = daySessions.size
            val minutes = daySessions.sumOf { it.durationMinutes }

            DayGroup(
                headerTitle = headerTitle,
                sessionCount = count,
                totalMinutes = minutes,
                sessions = daySessions
            )
        }
    }
}

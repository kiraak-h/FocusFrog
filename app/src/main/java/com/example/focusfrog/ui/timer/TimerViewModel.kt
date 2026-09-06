package com.example.focusfrog.ui.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusfrog.data.local.datastore.UserPreferencesRepository
import com.example.focusfrog.data.repository.FocusRepository
import com.example.focusfrog.data.repository.ShopRepository
import com.example.focusfrog.ui.components.FrogMood
import com.example.focusfrog.ui.components.FrogStage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val selectedDurationMinutes: Int = 25,
    val timeLeftSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isOnBreak: Boolean = false,
    val completedSessions: Int = 0,
    val bugsBalance: Int = 0,
    val frogMood: FrogMood = FrogMood.NEUTRAL,
    val hasHat: Boolean = false,
    val hasSunglasses: Boolean = false,
    val hasCrown: Boolean = false,
    val triggerJumpAnimation: Boolean = false,
    val showLeaveDialog: Boolean = false,
) {
    val frogStage: FrogStage
        get() = when {
            completedSessions >= 100 -> FrogStage.ROYAL_FROG
            completedSessions >= 30 -> FrogStage.BIG_FROG
            completedSessions >= 10 -> FrogStage.FROGLET
            else -> FrogStage.TADPOLE
        }
}

class TimerViewModel(
    private val focusRepository: FocusRepository,
    private val shopRepository: ShopRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Load initial user preferences (saved session duration)
        viewModelScope.launch {
            val savedDuration = userPreferencesRepository.selectedSessionLength.first()
            _uiState.update {
                it.copy(
                    selectedDurationMinutes = savedDuration,
                    timeLeftSeconds = savedDuration * 60
                )
            }
        }

        // Observe user stats from database in real-time
        viewModelScope.launch {
            focusRepository.userStatsFlow.collectLatest { stats ->
                val moodEnum = try {
                    FrogMood.valueOf(stats.frogMood)
                } catch (_: Exception) {
                    FrogMood.NEUTRAL
                }
                _uiState.update {
                    it.copy(
                        completedSessions = stats.totalSessions,
                        bugsBalance = stats.bugsBalance,
                        frogMood = moodEnum
                    )
                }
            }
        }

        // Observe equipped accessories from shop items
        viewModelScope.launch {
            shopRepository.allShopItems.collectLatest { items ->
                val hatEquipped = items.any { it.type == "HAT" && it.isEquipped }
                val shadesEquipped = items.any { it.type == "SUNGLASSES" && it.isEquipped }
                val crownEquipped = items.any { it.type == "CROWN" && it.isEquipped }
                _uiState.update {
                    it.copy(
                        hasHat = hatEquipped,
                        hasSunglasses = shadesEquipped,
                        hasCrown = crownEquipped
                    )
                }
            }
        }
    }

    fun selectDuration(minutes: Int) {
        if (_uiState.value.isRunning || _uiState.value.isOnBreak) return
        _uiState.update {
            it.copy(
                selectedDurationMinutes = minutes,
                timeLeftSeconds = minutes * 60,
                isOnBreak = false
            )
        }
        viewModelScope.launch {
            userPreferencesRepository.setSelectedSessionLength(minutes)
        }
    }

    fun toggleStartPause(context: Context) {
        val currentState = _uiState.value
        if (currentState.isRunning) {
            pauseTimer()
        } else {
            startTimer(context)
        }
    }

    private fun startTimer(context: Context) {
        _uiState.update { it.copy(isRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while ((_uiState.value.timeLeftSeconds > 0) && _uiState.value.isRunning) {
                delay(1000L)
                _uiState.update { it.copy(timeLeftSeconds = it.timeLeftSeconds - 1) }
            }
            if ((_uiState.value.timeLeftSeconds == 0) && _uiState.value.isRunning) {
                onTimerFinished(context)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun onResetClicked() {
        if (_uiState.value.isRunning && !_uiState.value.isOnBreak) {
            _uiState.update { it.copy(showLeaveDialog = true) }
        } else {
            resetTimer()
        }
    }

    fun confirmLeave() {
        timerJob?.cancel()
        val duration = _uiState.value.selectedDurationMinutes
        _uiState.update {
            it.copy(
                isRunning = false,
                showLeaveDialog = false,
                timeLeftSeconds = duration * 60,
                isOnBreak = false
            )
        }
        viewModelScope.launch {
            focusRepository.recordMidSessionLeavePenalty()
        }
    }

    fun dismissLeaveDialog() {
        _uiState.update { it.copy(showLeaveDialog = false) }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        val defaultSeconds = if (_uiState.value.isOnBreak) {
            5 * 60
        } else {
            _uiState.value.selectedDurationMinutes * 60
        }
        _uiState.update {
            it.copy(
                isRunning = false,
                timeLeftSeconds = defaultSeconds
            )
        }
    }

    fun skipBreak() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                isOnBreak = false,
                timeLeftSeconds = it.selectedDurationMinutes * 60
            )
        }
    }

    private fun onTimerFinished(context: Context) {
        timerJob?.cancel()
        if (_uiState.value.isOnBreak) {
            // Break finished -> reset to focus mode
            _uiState.update {
                it.copy(
                    isRunning = false,
                    isOnBreak = false,
                    timeLeftSeconds = it.selectedDurationMinutes * 60
                )
            }
        } else {
            // Focus session finished naturally -> record session in repository
            val durationMinutes = _uiState.value.selectedDurationMinutes
            _uiState.update {
                it.copy(
                    isRunning = false,
                    triggerJumpAnimation = true,
                    isOnBreak = true,
                    timeLeftSeconds = 5 * 60
                )
            }
            viewModelScope.launch {
                focusRepository.recordCompletedSession(context, durationMinutes)
            }
        }
    }

    fun onJumpAnimationFinished() {
        _uiState.update { it.copy(triggerJumpAnimation = false) }
    }
}

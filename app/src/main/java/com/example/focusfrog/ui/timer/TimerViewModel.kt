package com.example.focusfrog.ui.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusfrog.data.local.datastore.UserPreferencesRepository
import com.example.focusfrog.data.repository.FocusRepository
import com.example.focusfrog.data.repository.ShopRepository
import com.example.focusfrog.ui.components.FrogMood
import com.example.focusfrog.ui.components.FrogStage
import com.example.focusfrog.util.HapticFeedbackType
import com.example.focusfrog.util.HapticManager
import com.example.focusfrog.util.SoundEffect
import com.example.focusfrog.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val selectedDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val timeLeftSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isOnBreak: Boolean = false,
    val completedSessions: Int = 0,
    val bugsBalance: Int = 0,
    val frogMood: FrogMood = FrogMood.NEUTRAL,
    val hasHat: Boolean = false,
    val hasWizardHat: Boolean = false,
    val hasSunglasses: Boolean = false,
    val hasCrown: Boolean = false,
    val hasBowTie: Boolean = false,
    val hasHeadphones: Boolean = false,
    val hasLeafUmbrella: Boolean = false,
    val triggerJumpAnimation: Boolean = false,
    val showLeaveDialog: Boolean = false,
    val showCustomDurationDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val evolutionMessage: String? = null,
    val customDurationInput: String = "",
    val customDurationError: String? = null,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
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
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Load and observe saved focus session duration
        viewModelScope.launch {
            userPreferencesRepository.selectedSessionLength.collectLatest { savedDuration ->
                if (!_uiState.value.isRunning && !_uiState.value.isOnBreak) {
                    _uiState.update {
                        it.copy(
                            selectedDurationMinutes = savedDuration,
                            timeLeftSeconds = savedDuration * 60
                        )
                    }
                }
            }
        }

        // Load and observe saved break length
        viewModelScope.launch {
            userPreferencesRepository.breakLengthMinutes.collectLatest { breakMins ->
                _uiState.update { it.copy(breakDurationMinutes = breakMins) }
            }
        }

        // Load and observe sound preference
        viewModelScope.launch {
            userPreferencesRepository.soundEnabled.collectLatest { soundOn ->
                _uiState.update { it.copy(soundEnabled = soundOn) }
            }
        }

        // Load and observe haptics preference
        viewModelScope.launch {
            userPreferencesRepository.hapticsEnabled.collectLatest { hapticsOn ->
                _uiState.update { it.copy(hapticsEnabled = hapticsOn) }
            }
        }

        // Load and observe notifications preference
        viewModelScope.launch {
            userPreferencesRepository.notificationsEnabled.collectLatest { notifsOn ->
                _uiState.update { it.copy(notificationsEnabled = notifsOn) }
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
                val hatEquipped = items.any { it.id == "hat_frog" && it.isEquipped }
                val wizardHatEquipped = items.any { it.id == "wizard_hat" && it.isEquipped }
                val shadesEquipped = items.any { it.type == "SUNGLASSES" && it.isEquipped }
                val crownEquipped = items.any { it.type == "CROWN" && it.isEquipped }
                val bowTieEquipped = items.any { it.type == "NECK" && it.isEquipped }
                val headphonesEquipped = items.any { it.type == "HEAD" && it.isEquipped }
                val leafUmbrellaEquipped = items.any { it.type == "HAND" && it.isEquipped }

                _uiState.update {
                    it.copy(
                        hasHat = hatEquipped,
                        hasWizardHat = wizardHatEquipped,
                        hasSunglasses = shadesEquipped,
                        hasCrown = crownEquipped,
                        hasBowTie = bowTieEquipped,
                        hasHeadphones = headphonesEquipped,
                        hasLeafUmbrella = leafUmbrellaEquipped
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

    fun openCustomDurationDialog() {
        if (_uiState.value.isRunning || _uiState.value.isOnBreak) return
        _uiState.update {
            it.copy(
                showCustomDurationDialog = true,
                customDurationInput = it.selectedDurationMinutes.toString(),
                customDurationError = null
            )
        }
    }

    fun updateCustomDurationInput(input: String) {
        _uiState.update { it.copy(customDurationInput = input, customDurationError = null) }
    }

    fun dismissCustomDurationDialog() {
        _uiState.update { it.copy(showCustomDurationDialog = false, customDurationError = null) }
    }

    fun applyCustomDuration() {
        val input = _uiState.value.customDurationInput.trim()
        val minutes = input.toIntOrNull()
        if (minutes == null || minutes !in 1..120) {
            _uiState.update {
                it.copy(customDurationError = "Enter a duration between 1 and 120 minutes")
            }
            return
        }
        selectDuration(minutes)
        _uiState.update { it.copy(showCustomDurationDialog = false, customDurationError = null) }
    }

    fun openSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun dismissSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun dismissEvolutionDialog() {
        _uiState.update { it.copy(evolutionMessage = null) }
    }

    fun updateBreakDuration(minutes: Int) {
        if (minutes !in 1..30) return
        viewModelScope.launch {
            userPreferencesRepository.setBreakLengthMinutes(minutes)
        }
    }

    fun toggleSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setSoundEnabled(enabled)
        }
    }

    fun toggleHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setHapticsEnabled(enabled)
        }
    }

    fun toggleNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun toggleStartPause(context: Context) {
        HapticManager.performHaptic(context, HapticFeedbackType.LIGHT_TICK, _uiState.value.hapticsEnabled)
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

    fun onResetClicked(context: Context) {
        HapticManager.performHaptic(context, HapticFeedbackType.LIGHT_TICK, _uiState.value.hapticsEnabled)
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
            _uiState.value.breakDurationMinutes * 60
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
            // Focus session finished naturally -> sound, haptic, record session, trigger jump
            SoundManager.playSound(SoundEffect.SESSION_COMPLETE, _uiState.value.soundEnabled)
            SoundManager.playSound(SoundEffect.HAPPY_JUMP, _uiState.value.soundEnabled)
            HapticManager.performHaptic(context, HapticFeedbackType.DOUBLE_TICK_COMPLETE, _uiState.value.hapticsEnabled)

            val durationMinutes = _uiState.value.selectedDurationMinutes
            val breakMins = _uiState.value.breakDurationMinutes

            val oldSessions = _uiState.value.completedSessions
            val newSessions = oldSessions + 1

            val evolutionMsg = when {
                oldSessions < 10 && newSessions >= 10 -> "Lily evolved into a Froglet! 🫧 → 🌿"
                oldSessions < 30 && newSessions >= 30 -> "Lily evolved into a Big Frog! 🌿 → 🐸"
                oldSessions < 100 && newSessions >= 100 -> "Lily evolved into a Royal Frog! 🐸 → 👑"
                else -> null
            }

            _uiState.update {
                it.copy(
                    isRunning = false,
                    triggerJumpAnimation = true,
                    isOnBreak = true,
                    timeLeftSeconds = breakMins * 60,
                    evolutionMessage = evolutionMsg
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

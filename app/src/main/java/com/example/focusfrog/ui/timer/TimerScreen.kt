package com.example.focusfrog.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusfrog.R
import com.example.focusfrog.ui.components.CustomDurationDialog
import com.example.focusfrog.ui.components.EvolutionDialog
import com.example.focusfrog.ui.components.FrogAvatar
import com.example.focusfrog.ui.components.FrogMood
import com.example.focusfrog.ui.components.FrogStage
import com.example.focusfrog.ui.components.LeaveDialog
import com.example.focusfrog.ui.components.SettingsDialog
import com.example.focusfrog.ui.theme.BugAmber
import com.example.focusfrog.ui.theme.FrogGreen
import java.util.Locale

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showLeaveDialog) {
        LeaveDialog(
            onConfirm = { viewModel.confirmLeave() },
            onDismiss = { viewModel.dismissLeaveDialog() }
        )
    }

    if (uiState.showCustomDurationDialog) {
        CustomDurationDialog(
            input = uiState.customDurationInput,
            error = uiState.customDurationError,
            onInputChange = { viewModel.updateCustomDurationInput(it) },
            onApply = { viewModel.applyCustomDuration() },
            onDismiss = { viewModel.dismissCustomDurationDialog() }
        )
    }

    if (uiState.showSettingsDialog) {
        SettingsDialog(
            breakDurationMinutes = uiState.breakDurationMinutes,
            soundEnabled = uiState.soundEnabled,
            hapticsEnabled = uiState.hapticsEnabled,
            notificationsEnabled = uiState.notificationsEnabled,
            onBreakDurationChange = { viewModel.updateBreakDuration(it) },
            onSoundToggle = { viewModel.toggleSoundEnabled(it) },
            onHapticsToggle = { viewModel.toggleHapticsEnabled(it) },
            onNotificationsToggle = { viewModel.toggleNotificationsEnabled(it) },
            onDismiss = { viewModel.dismissSettingsDialog() }
        )
    }

    uiState.evolutionMessage?.let { message ->
        EvolutionDialog(
            message = message,
            onDismiss = { viewModel.dismissEvolutionDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Settings Icon & Bugs Balance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.openSettingsDialog() }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings_title),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BugAmber.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.bugs_count, uiState.bugsBalance),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Center Content: Frog Avatar & Stage Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FrogAvatar(
                stage = uiState.frogStage,
                mood = uiState.frogMood,
                size = 180.dp,
                hasHat = uiState.hasHat,
                hasWizardHat = uiState.hasWizardHat,
                hasSunglasses = uiState.hasSunglasses,
                hasCrown = uiState.hasCrown,
                hasBowTie = uiState.hasBowTie,
                hasHeadphones = uiState.hasHeadphones,
                hasLeafUmbrella = uiState.hasLeafUmbrella,
                triggerJump = uiState.triggerJumpAnimation,
                onJumpFinished = { viewModel.onJumpAnimationFinished() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = stringResource(id = getStageStringRes(uiState.frogStage)),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = getMoodStringRes(uiState.frogMood)),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }

        // Timer Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Duration Picker Chips (Only when not running and not on break)
            if (!uiState.isRunning && !uiState.isOnBreak) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    val presetOptions = listOf(5, 15, 25, 50)
                    val currentMins = uiState.selectedDurationMinutes
                    val isCustomActive = currentMins !in presetOptions

                    presetOptions.forEach { duration ->
                        val isSelected = currentMins == duration
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectDuration(duration) },
                            label = {
                                Text(
                                    text = "${duration}m",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FrogGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.Transparent,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    // Custom Duration Chip
                    FilterChip(
                        selected = isCustomActive,
                        onClick = { viewModel.openCustomDurationDialog() },
                        label = {
                            Text(
                                text = if (isCustomActive) "${currentMins}m" else stringResource(id = R.string.custom),
                                color = if (isCustomActive) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isCustomActive) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FrogGreen,
                            selectedLabelColor = Color.White,
                            containerColor = Color.Transparent,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            } else if (uiState.isOnBreak) {
                Text(
                    text = stringResource(id = R.string.on_break),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // MM:SS / HH:MM:SS Timer Display
            Text(
                text = formatTimerText(uiState.timeLeftSeconds),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.toggleStartPause(context) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = if (uiState.isRunning) {
                            stringResource(id = R.string.pause)
                        } else {
                            stringResource(id = R.string.start)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.onResetClicked(context) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.reset),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // In Break mode, show "Skip Break" button
            if (uiState.isOnBreak) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { viewModel.skipBreak() }) {
                    Text(
                        text = stringResource(id = R.string.skip_break),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Bottom: Completed Sessions Counter
        Text(
            text = stringResource(id = R.string.sessions_completed, uiState.completedSessions),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

private fun formatTimerText(secondsRemaining: Int): String {
    val hours = secondsRemaining / 3600
    val minutes = (secondsRemaining % 3600) / 60
    val seconds = secondsRemaining % 60
    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

private fun getStageStringRes(stage: FrogStage): Int {
    return when (stage) {
        FrogStage.TADPOLE -> R.string.stage_tadpole
        FrogStage.FROGLET -> R.string.stage_froglet
        FrogStage.BIG_FROG -> R.string.stage_big_frog
        FrogStage.ROYAL_FROG -> R.string.stage_royal_frog
    }
}

private fun getMoodStringRes(mood: FrogMood): Int {
    return when (mood) {
        FrogMood.HAPPY -> R.string.mood_happy
        FrogMood.NEUTRAL -> R.string.mood_neutral
        FrogMood.HUNGRY -> R.string.mood_hungry
    }
}

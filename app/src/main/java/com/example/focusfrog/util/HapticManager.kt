package com.example.focusfrog.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class HapticFeedbackType {
    LIGHT_TICK,
    DOUBLE_TICK_COMPLETE,
    SUCCESS_PURCHASE
}

object HapticManager {

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    @Suppress("MissingPermission")
    fun performHaptic(context: Context, type: HapticFeedbackType, isHapticsEnabled: Boolean) {
        if (!isHapticsEnabled) return
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        when (type) {
            HapticFeedbackType.LIGHT_TICK -> {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
            HapticFeedbackType.DOUBLE_TICK_COMPLETE -> {
                val timings = longArrayOf(0, 30, 60, 30)
                val amplitudes = intArrayOf(0, 180, 0, 180)
                vibrator.vibrate(
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                )
            }
            HapticFeedbackType.SUCCESS_PURCHASE -> {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        }
    }
}

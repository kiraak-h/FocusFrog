package com.example.focusfrog.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.focusfrog.FocusFrogApplication
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? FocusFrogApplication ?: return Result.success()
        val prefs = app.userPreferencesRepository
        val isNotificationsEnabled = prefs.notificationsEnabled.first()

        if (!isNotificationsEnabled) {
            return Result.success()
        }

        val focusRepo = app.focusRepository
        val stats = focusRepo.getInitialUserStats()
        val todayStr = LocalDate.now().toString()

        // If a session was already completed today, do NOT send daily reminder!
        if (stats.lastSessionDate == todayStr) {
            return Result.success()
        }

        NotificationHelper.showDailyReminderNotification(applicationContext)
        return Result.success()
    }
}

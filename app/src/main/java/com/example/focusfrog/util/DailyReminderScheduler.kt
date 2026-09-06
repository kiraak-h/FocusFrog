package com.example.focusfrog.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object DailyReminderScheduler {

    private const val WORK_NAME = "DailyEveningReminderWork"

    fun scheduleDailyReminder(context: Context) {
        val now = LocalDateTime.now()
        var targetTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(18, 0))
        if (now.isAfter(targetTime)) {
            targetTime = targetTime.plusDays(1)
        }

        val initialDelay = Duration.between(now, targetTime)

        val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /**
     * Test Helper: Enqueues [DailyReminderWorker] immediately via a OneTimeWorkRequest
     * to verify worker branches without waiting until 18:00.
     */
    fun triggerTestReminder(context: Context) {
        val testRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>().build()
        WorkManager.getInstance(context).enqueue(testRequest)
    }
}

package com.example.focusfrog.data.repository

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.example.focusfrog.FocusFrogApplication
import com.example.focusfrog.data.local.db.SessionDao
import com.example.focusfrog.data.local.db.SessionEntity
import com.example.focusfrog.data.local.db.UserStatsDao
import com.example.focusfrog.data.local.db.UserStatsEntity
import com.example.focusfrog.ui.components.FrogMood
import com.example.focusfrog.util.FocusFrogWidget
import com.example.focusfrog.util.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

class FocusRepository(
    private val sessionDao: SessionDao,
    private val userStatsDao: UserStatsDao,
) {

    val userStatsFlow: Flow<UserStatsEntity> = userStatsDao.getUserStatsFlow().map { stats ->
        val currentStats = stats ?: UserStatsEntity()
        val calculatedMood = calculateMoodFromDate(currentStats.lastSessionDate)
        if (calculatedMood.name != currentStats.frogMood) {
            val updated = currentStats.copy(frogMood = calculatedMood.name)
            userStatsDao.insertOrUpdateUserStats(updated)
            updated
        } else {
            currentStats
        }
    }

    val recentSessionsFlow: Flow<List<SessionEntity>> = sessionDao.getRecentSessions()

    fun getTodaySessionsCountFlow(): Flow<Int> {
        val todayStr = LocalDate.now().toString()
        return sessionDao.getSessionsCountForDate(todayStr)
    }

    suspend fun addTestBugs(amount: Int = 300) {
        val stats = getInitialUserStats()
        val updated = stats.copy(bugsBalance = stats.bugsBalance + amount)
        userStatsDao.insertOrUpdateUserStats(updated)
    }

    suspend fun removeTestBugs(amount: Int = 300) {
        val stats = getInitialUserStats()
        val newBalance = max(0, stats.bugsBalance - amount)
        val updated = stats.copy(bugsBalance = newBalance)
        userStatsDao.insertOrUpdateUserStats(updated)
    }

    suspend fun getInitialUserStats(): UserStatsEntity {
        var stats = userStatsDao.getUserStatsOnce()
        if (stats == null) {
            stats = UserStatsEntity(
                id = 1,
                bugsBalance = 0,
                totalSessions = 0,
                totalMinutes = 0,
                currentStreak = 0,
                bestStreak = 0,
                lastSessionDate = null,
                frogMood = "NEUTRAL"
            )
            userStatsDao.insertOrUpdateUserStats(stats)
        } else {
            val calculatedMood = calculateMoodFromDate(stats.lastSessionDate)
            if (calculatedMood.name != stats.frogMood) {
                stats = stats.copy(frogMood = calculatedMood.name)
                userStatsDao.insertOrUpdateUserStats(stats)
            }
        }
        return stats
    }

    suspend fun recordCompletedSession(context: Context, durationMinutes: Int) {
        val today = LocalDate.now()
        val todayStr = today.toString()
        val yesterdayStr = today.minusDays(1).toString()

        val currentStats = getInitialUserStats()

        val newCurrentStreak = when (currentStats.lastSessionDate) {
            todayStr -> currentStats.currentStreak
            yesterdayStr -> currentStats.currentStreak + 1
            else -> 1
        }
        val newBestStreak = max(currentStats.bestStreak, newCurrentStreak)

        val updatedStats = UserStatsEntity(
            id = 1,
            bugsBalance = currentStats.bugsBalance + 10,
            totalSessions = currentStats.totalSessions + 1,
            totalMinutes = currentStats.totalMinutes + durationMinutes,
            currentStreak = newCurrentStreak,
            bestStreak = newBestStreak,
            lastSessionDate = todayStr,
            frogMood = FrogMood.HAPPY.name
        )

        userStatsDao.insertOrUpdateUserStats(updatedStats)

        val session = SessionEntity(
            timestamp = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            completedDate = todayStr
        )
        sessionDao.insertSession(session)

        val appContext = context.applicationContext

        // Update home-screen widget
        try {
            FocusFrogWidget().updateAll(appContext)
        } catch (_: Exception) {
        }

        // Post session complete notification if app is in background and notifications are enabled
        if (!NotificationHelper.isAppInForeground()) {
            val app = appContext as? FocusFrogApplication
            if (app != null) {
                val isEnabled = app.userPreferencesRepository.notificationsEnabled.first()
                if (isEnabled) {
                    NotificationHelper.showSessionCompleteNotification(appContext)
                }
            }
        }
    }

    suspend fun recordMidSessionLeavePenalty() {
        val currentStats = getInitialUserStats()
        val updatedStats = currentStats.copy(frogMood = FrogMood.HUNGRY.name)
        userStatsDao.insertOrUpdateUserStats(updatedStats)
    }

    private fun calculateMoodFromDate(lastSessionDateStr: String?): FrogMood {
        if (lastSessionDateStr.isNullOrEmpty()) return FrogMood.NEUTRAL
        return try {
            val lastDate = LocalDate.parse(lastSessionDateStr)
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(lastDate, today)
            when {
                days <= 0 -> FrogMood.HAPPY
                days == 1L -> FrogMood.NEUTRAL
                else -> FrogMood.HUNGRY
            }
        } catch (_: Exception) {
            FrogMood.NEUTRAL
        }
    }
}

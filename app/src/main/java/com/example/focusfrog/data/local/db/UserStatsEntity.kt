package com.example.focusfrog.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val bugsBalance: Int = 0,
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastSessionDate: String? = null,
    val frogMood: String = "NEUTRAL"
)

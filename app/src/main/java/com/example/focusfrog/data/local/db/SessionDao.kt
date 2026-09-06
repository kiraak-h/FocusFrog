package com.example.focusfrog.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT COUNT(*) FROM sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT SUM(durationMinutes) FROM sessions")
    fun getTotalMinutesSum(): Flow<Int?>

    @Query("SELECT * FROM sessions WHERE completedDate = :dateStr")
    suspend fun getSessionsForDate(dateStr: String): List<SessionEntity>

    @Query("SELECT COUNT(*) FROM sessions WHERE completedDate = :dateStr")
    fun getSessionsCountForDate(dateStr: String): Flow<Int>
}

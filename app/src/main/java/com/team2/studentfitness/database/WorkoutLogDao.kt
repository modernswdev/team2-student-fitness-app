package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<WorkoutLog>>

    @Insert
    suspend fun insert(log: WorkoutLog)

    @Query("SELECT * FROM workout_logs WHERE timestamp >= :startOfDay")
    suspend fun getLogsForToday(startOfDay: Long): List<WorkoutLog>
}

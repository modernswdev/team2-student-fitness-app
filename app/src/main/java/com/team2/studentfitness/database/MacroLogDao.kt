package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MacroLogDao {
    @Query("SELECT * FROM macro_logs ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<MacroLog>>

    @Insert
    suspend fun insert(log: MacroLog)

    @Query("DELETE FROM macro_logs WHERE id = :id")
    suspend fun deleteById(id: Int)
}

package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MentalHealthDao {
    @Query("SELECT * FROM mental_health ORDER BY timestamp DESC")
    suspend fun getAll(): List<MentalHealth>

    @Insert
    suspend fun insert(mentalHealth: MentalHealth)

    @Query("SELECT * FROM mental_health ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): MentalHealth?
}

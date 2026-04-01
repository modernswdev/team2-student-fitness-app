package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MentalHealthDao {
    @Query("SELECT * FROM MentalHealth ORDER BY timestamp DESC")
    suspend fun getAll(): List<MentalHealth>

    @Query("SELECT * FROM MentalHealth WHERE id = :id")
    suspend fun getById(id: Int): MentalHealth

    @Insert()
    suspend fun insert(MentalHealth: MentalHealth)

    @Delete()
    suspend fun delete(MentalHealth: MentalHealth)

    //Get most recent entry
    @Query("SELECT * FROM MentalHealth ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): MentalHealth?

    //Get all data from timestamp
    @Query("SELECT * FROM MentalHealth WHERE timestamp = :timestamp")
    suspend fun getByTimestamp(timestamp: Long): MentalHealth

    //Get ID from timestamp
    @Query("SELECT id FROM MentalHealth WHERE timestamp = :timestamp")
    suspend fun getIdByTimestamp(timestamp: Long): Int

    //Change mood at id
    @Query("UPDATE MentalHealth SET mood = :newMood WHERE id = :id")
    suspend fun updateMood(newMood: String, id: Int)

    //Change reflection at id
    @Query("UPDATE MentalHealth SET reflection = :newReflection WHERE id = :id")
    suspend fun updateReflection(newReflection: String, id: Int)
}

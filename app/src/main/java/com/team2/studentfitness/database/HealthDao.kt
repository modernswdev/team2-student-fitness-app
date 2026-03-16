package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HealthDao {
    @Query("SELECT * FROM healthdata")
    suspend fun getAll(): List<HealthData>

    @Query("SELECT * FROM healthdata WHERE id = :id")
    suspend fun getById(id: Int): HealthData

    @Query("SELECT * FROM healthdata WHERE timestamp = :timestamp")
    suspend fun getByTimestamp(timestamp: String): List<HealthData>

    @Insert()
    suspend fun insert(healthData: HealthData)

    @Delete()
    suspend fun delete(healthData: HealthData)

    @Query("DELETE FROM healthdata WHERE id = :id")
    suspend fun deleteById(id: Int)

    // ----- Heart Rate -----
    @Query("SELECT heartRate FROM healthdata WHERE timestamp = :timestamp")
    suspend fun getHeartRate(timestamp: String): Int?

    // ----- Body Temp -----
    @Query("SELECT bodyTemp FROM healthdata WHERE timestamp = :timestamp")
    suspend fun getBodyTemp(timestamp: String): Int?

    // ----- Step Count -----
    @Query("UPDATE healthdata SET stepCount = 0 WHERE id = :id")
    suspend fun resetStepCount(id: Int)

    @Query("SELECT stepCount FROM healthdata WHERE id = :id")
    suspend fun getStepCount(id: Int): Int?

    @Query("SELECT totalSteps FROM healthdata WHERE id = :id")
    suspend fun getTotalSteps(id: Int): Int?

    @Query("UPDATE healthdata SET stepCount = stepCount + :newCurrentSteps WHERE id = :id")
    suspend fun updateCurrentSteps(newCurrentSteps: Int, id: Int)

    @Query("UPDATE healthdata SET totalSteps = totalSteps + :newTotalSteps WHERE id = :id")
    suspend fun updateTotalSteps(newTotalSteps: Int, id: Int)

    @Query("UPDATE healthdata SET totalSteps = totalSteps + stepCount WHERE id = :id")
    suspend fun addCurrentSteps(id: Int)
}

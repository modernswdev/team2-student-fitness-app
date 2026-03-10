package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HealthDao {
    @Query("SELECT * FROM healthdata")
    fun getAll(): List<HealthData>

    @Query("SELECT * FROM healthdata WHERE id = :id")
    fun getById(id: Int): HealthData

    @Query("SELECT * FROM healthdata WHERE timestamp = :timestamp")
    fun getByTimestamp(timestamp: String): List<HealthData>

    @Insert()
    fun insert(healthData: HealthData)

    @Delete()
    fun delete(healthData: HealthData)

    @Query("DELETE FROM healthdata WHERE id = :id")
    fun deleteById(id: Int)

    // ----- Heart Rate -----
    @Query("SELECT heartRate FROM healthdata WHERE timestamp = :timestamp")
    fun getHeartRate(timestamp: String): Int?

    // ----- Body Temp -----
    @Query("SELECT bodyTemp FROM healthdata WHERE timestamp = :timestamp")
    fun getBodyTemp(timestamp: String): Int?

    // ----- Step Count -----
    @Query("UPDATE healthdata SET stepCount = 0 WHERE id = :id")
    fun resetStepCount(id: Int)

    @Query("SELECT stepCount FROM healthdata WHERE id = :id")
    fun getStepCount(id: Int): Int?

    @Query("SELECT totalSteps FROM healthdata WHERE id = :id")
    fun getTotalSteps(id: Int): Int?

    @Query("UPDATE healthdata SET stepCount = stepCount + :newCurrentSteps WHERE id = :id")
    fun updateCurrentSteps(newCurrentSteps: Int, id: Int)

    @Query("UPDATE healthdata SET totalSteps = totalSteps + :newTotalSteps WHERE id = :id")
    fun updateTotalSteps(newTotalSteps: Int, id: Int)

    @Query("UPDATE healthdata SET totalSteps = totalSteps + stepCount WHERE id = :id")
    fun addCurrentSteps(id: Int)
}

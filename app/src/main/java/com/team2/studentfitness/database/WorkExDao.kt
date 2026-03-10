package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete

@Dao
interface WorkExDao {
    //Put queries here
    @Query("SELECT * FROM WorkEx")
    fun getAll(): List<WorkEx>

    @Query("SELECT * FROM WorkEx WHERE uid = :uid")
    fun getById(uid: Int): WorkEx

    @Query("SELECT * FROM WorkEx WHERE workoutName = :name")
    fun getByName(name: String): List<WorkEx>

    @Insert()
    fun insert(workouts: WorkEx)

    @Delete()
    fun delete(workouts: WorkEx)

    @Query("DELETE FROM WorkEx WHERE uid = :uid")
    fun deleteById(uid: Int)

    // ----- Edit -----
    //Edit reps
    @Query("UPDATE WorkEx SET reps = :reps WHERE uid = :uid")
    fun editReps(uid: Int, reps: Int)

    //Edit sets
    @Query("UPDATE WorkEx SET sets = :sets WHERE uid = :uid")
    fun editSets(uid: Int, sets: Int)

    //Edit rest time
    @Query("UPDATE WorkEx SET restTime = :restTime WHERE uid = :uid")
    fun editRestTime(uid: Int, restTime: Int)

    //Edit order
    @Query("UPDATE WorkEx SET exOrder = :exOrder WHERE uid = :uid")
    fun editOrder(uid: Int, exOrder: Int)
}
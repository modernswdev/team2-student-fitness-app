package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Transaction

@Dao
interface WorkExDao {
    //Put queries here
    @Query("SELECT * FROM WorkEx")
    suspend fun getAll(): List<WorkEx>

    @Query("SELECT * FROM WorkEx WHERE uid = :uid")
    suspend fun getById(uid: Int): WorkEx

    @Query("SELECT * FROM WorkEx WHERE workoutName = :name")
    suspend fun getByName(name: String): List<WorkEx>

    @Insert()
    suspend fun insert(workouts: WorkEx)

    @Insert()
    suspend fun insertAll(workExList: List<WorkEx>)

    @Delete()
    suspend fun delete(workouts: WorkEx)

    @Query("DELETE FROM WorkEx WHERE uid = :uid")
    suspend fun deleteById(uid: Int)

    // ----- Edit -----
    //Edit reps
    @Query("UPDATE WorkEx SET reps = :reps WHERE uid = :uid")
    suspend fun editReps(uid: Int, reps: Int)

    //Edit sets
    @Query("UPDATE WorkEx SET sets = :sets WHERE uid = :uid")
    suspend fun editSets(uid: Int, sets: Int)

    //Edit rest time
    @Query("UPDATE WorkEx SET restTime = :restTime WHERE uid = :uid")
    suspend fun editRestTime(uid: Int, restTime: Int)

    //Edit order
    @Query("UPDATE WorkEx SET exOrder = :exOrder WHERE uid = :uid")
    suspend fun editOrder(uid: Int, exOrder: Int)

    @Transaction
    suspend fun insertOrderedWorkEx(inputs: List<WorkExInput>) {
        val workExList = inputs.mapIndexed { index, input ->
            WorkEx(
                uid = 0,
                workoutName = input.workoutName,
                workoutID = input.workoutID,
                exerciseID = input.exerciseID,
                reps = input.reps,
                sets = input.sets,
                restTime = input.restTime,
                order = index + 1
            )
        }
        insertAll(workExList)
    }
}

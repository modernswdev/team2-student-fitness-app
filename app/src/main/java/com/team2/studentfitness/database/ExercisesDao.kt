package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete

@Dao
interface ExercisesDao {
    @Query("SELECT * FROM Exercises")
    suspend fun getAll(): List<Exercises>

    @Query("SELECT * FROM Exercises WHERE uid = :uid")
    suspend fun getById(uid: Int): Exercises

    @Query("SELECT * FROM Exercises WHERE name = :name")
    suspend fun getByName(name: String): List<Exercises>

    @Query("SELECT * FROM Exercises WHERE muscleGroup = :muscleGroup")
    suspend fun getByMuscleGroup(muscleGroup: String): List<Exercises>

    @Query("SELECT * FROM Exercises WHERE difficulty = :difficulty")
    suspend fun getByDifficulty(difficulty: Int): List<Exercises>

    @Insert()
    suspend fun insert(workouts: Exercises)

    @Delete()
    suspend fun delete(workouts: Exercises)

    @Query("DELETE FROM Exercises WHERE uid = :uid")
    suspend fun deleteById(uid: Int)
}

package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete

@Dao
interface ExercisesDao {
    @Query("SELECT * FROM Exercises")
    fun getAll(): List<Exercises>

    @Query("SELECT * FROM Exercises WHERE uid = :uid")
    fun getById(uid: Int): Exercises

    @Query("SELECT * FROM Exercises WHERE name = :name")
    fun getByName(name: String): List<Exercises>

    @Insert()
    fun insert(workouts: Exercises)

    @Delete()
    fun delete(workouts: Exercises)

    @Query("DELETE FROM Exercises WHERE uid = :uid")
    fun deleteById(uid: Int)

}
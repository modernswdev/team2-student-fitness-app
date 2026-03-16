package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete

@Dao
interface WorkoutsDao {
    //Put queries here
    @Query("SELECT * FROM Workouts")
    suspend fun getAll(): List<Workouts>

    @Query("SELECT * FROM Workouts WHERE uid = :uid")
    suspend fun getById(uid: Int): Workouts

    @Query("SELECT * FROM Workouts WHERE name = :name")
    suspend fun getByName(name: String): List<Workouts>

    @Insert()
    suspend fun insert(workouts: Workouts)

    @Delete()
    suspend fun delete(workouts: Workouts)

    @Query("DELETE FROM Workouts WHERE uid = :uid")
    suspend fun deleteById(uid: Int)

    // ----- Select all -----
    //Select all by type
    @Query("SELECT * FROM Workouts WHERE type = :type")
    suspend fun getByType(type: Int): List<Workouts>

    //Select all by duration
    @Query("SELECT * FROM Workouts WHERE duration = :duration")
    suspend fun getByDuration(duration: Int): List<Workouts>

    //Select all by focus
    @Query("SELECT * FROM Workouts WHERE focus = :focus")
    suspend fun getByFocus(focus: String): List<Workouts>

    //Select all by difficulty
    @Query("SELECT * FROM Workouts WHERE difficulty = :difficulty")
    suspend fun getByDifficulty(difficulty: Int): List<Workouts>

    //Select all by split
    @Query("SELECT * FROM Workouts WHERE split = :split")
    suspend fun getBySplit(split: String): List<Workouts>

    // ----- Select by ID -----
    //Get type by ID
    @Query("SELECT type FROM Workouts WHERE uid = :uid")
    suspend fun getType(uid: Int): Int

    //Get duration by ID
    @Query("SELECT duration FROM Workouts WHERE uid = :uid")
    suspend fun getDuration(uid: Int): Int

    //Get focus by ID
    @Query("SELECT focus FROM Workouts WHERE uid = :uid")
    suspend fun getFocus(uid: Int): String

    //Get difficulty by ID
    @Query("SELECT difficulty FROM Workouts WHERE uid = :uid")
    suspend fun getDifficulty(uid: Int): Int

    //Get split by ID
    @Query("SELECT split FROM Workouts WHERE uid = :uid")
    suspend fun getSplit(uid: Int): String
}

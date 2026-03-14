package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface UserDao {
    //Select all users
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    //Select all users with same name
    @Query("SELECT * FROM user where name = :name")
    suspend fun findByName(name: String): User?

    //Select user by ID
    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUserById(uid: Int): User?

    //Set new login count given user ID
    @Query("UPDATE user SET loginCount = :newLoginCount WHERE uid = :uid")
    suspend fun updateLoginCount(newLoginCount: Int, uid: Int)

    //Increment login count by 1 given user ID
    @Query("UPDATE user SET loginCount = loginCount + 1 WHERE uid = :uid")
    suspend fun incrementLoginCount(uid: Int)

    //Add new user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    //Delete user
    @Delete
    suspend fun delete(user: User)
}

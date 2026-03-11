package com.team2.studentfitness.database.databaseTest

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

//This dao is for testing purposes only
@Dao
interface UserDao {
    //Select all users
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    //Select all users with same name
    @Query("SELECT * FROM user where name = :name")
    fun findByName(name: String): User?

    //Select user by ID
    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserById(uid: Int): User?

    //Set new login count given user ID
    @Query("UPDATE user SET loginCount = :newLoginCount WHERE uid = :uid")
    fun updateLoginCount(newLoginCount: Int, uid: Int)

    //Increment login count by 1 given user ID
    @Query("UPDATE user SET loginCount = loginCount + 1 WHERE uid = :uid")
    fun incrementLoginCount(uid: Int)

    //Add new user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    //Delete user
    @Delete
    fun delete(user: User)
}
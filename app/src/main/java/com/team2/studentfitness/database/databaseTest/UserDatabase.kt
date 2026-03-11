package com.team2.studentfitness.database.databaseTest


import androidx.room.Database;
import androidx.room.RoomDatabase;

//This database is for testing only
@Database(entities = [User::class], version = 1)
public abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
package com.team2.studentfitness.database


import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = [User::class], version = 1)
public abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
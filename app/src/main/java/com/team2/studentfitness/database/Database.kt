package com.team2.studentfitness.database


import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = [UserSettings::class, WorkEx::class, Exercises::class, HealthData::class, Workouts::class], version = 1)
public abstract class Database : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun workoutsDao(): WorkoutsDao
    abstract fun exercisesDao(): ExercisesDao
    abstract fun workExDao(): WorkExDao
    abstract fun healthDao(): HealthDao
}
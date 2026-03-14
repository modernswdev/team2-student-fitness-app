package com.team2.studentfitness.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@androidx.room.Database(entities = [UserSettings::class, WorkEx::class, Exercises::class, HealthData::class, Workouts::class], version = 1)
public abstract class Database : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun workoutsDao(): WorkoutsDao
    abstract fun exercisesDao(): ExercisesDao
    abstract fun workExDao(): WorkExDao
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getDatabase(context: Context): Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "student_fitness_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // This is where you can pre-populate the database
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

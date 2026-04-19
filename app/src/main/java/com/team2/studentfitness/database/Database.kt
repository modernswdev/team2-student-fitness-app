package com.team2.studentfitness.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.team2.studentfitness.R
import java.io.BufferedReader
import java.io.InputStreamReader

@androidx.room.Database(entities = [UserSettings::class, WorkEx::class, Exercises::class, HealthData::class, Workouts::class, MentalHealth::class, MacroLog::class, WorkoutLog::class], version = 6)
public abstract class Database : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun workoutsDao(): WorkoutsDao
    abstract fun exercisesDao(): ExercisesDao
    abstract fun workExDao(): WorkExDao
    abstract fun healthDao(): HealthDao
    abstract fun mentalHealthDao(): MentalHealthDao
    abstract fun macroLogDao(): MacroLogDao
    abstract fun workoutLogDao(): WorkoutLogDao

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
                .addCallback(getDatabaseCallback(context))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabaseCallback(context: Context): RoomDatabase.Callback {
            return object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    prepopulateExercises(context, db)
                    prepopulateWorkouts(context, db)
                    prepopulateWorkEx(context, db)
                }
            }
        }

        private fun prepopulateExercises(context: Context, db: SupportSQLiteDatabase) {
            try {
                val inputStream = context.resources.openRawResource(R.raw.exercise)
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.use { r ->
                    r.readLine() // Skip header
                    var line: String? = r.readLine()
                    while (line != null) {
                        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                        if (parts.size >= 5) {
                            val name = clean(parts[1])
                            val muscleGroup = clean(parts[2])
                            val difficultyStr = clean(parts[3])
                            val description = clean(parts[4])

                            val difficulty = when (difficultyStr.lowercase()) {
                                "beginner" -> 0
                                "intermediate" -> 1
                                "advanced" -> 2
                                else -> 0
                            }

                            db.execSQL(
                                "INSERT INTO Exercises (name, muscleGroup, difficulty, description) VALUES (?, ?, ?, ?)",
                                arrayOf<Any>(name, muscleGroup, difficulty, description)
                            )
                        }
                        line = r.readLine()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun prepopulateWorkouts(context: Context, db: SupportSQLiteDatabase) {
            try {
                val inputStream = context.resources.openRawResource(R.raw.workouts)
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.use { r ->
                    r.readLine() // Skip header
                    var line: String? = r.readLine()
                    while (line != null) {
                        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                        if (parts.size >= 7) {
                            val name = clean(parts[1])
                            val duration = clean(parts[2]).toIntOrNull() ?: 0
                            val focus = clean(parts[3])
                            val typeStr = clean(parts[4])
                            val difficultyStr = clean(parts[5])
                            val split = clean(parts[6])

                            val type = when (typeStr.lowercase()) {
                                "recovery" -> 0
                                "cardio" -> 1
                                "strength" -> 2
                                "mixed" -> 3
                                else -> 3
                            }

                            val difficulty = when (difficultyStr.lowercase()) {
                                "beginner" -> 0
                                "intermediate" -> 1
                                "advanced" -> 2
                                else -> 0
                            }

                            db.execSQL(
                                "INSERT INTO Workouts (name, duration, focus, type, difficulty, split) VALUES (?, ?, ?, ?, ?, ?)",
                                arrayOf<Any>(name, duration, focus, type, difficulty, split)
                            )
                        }
                        line = r.readLine()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun prepopulateWorkEx(context: Context, db: SupportSQLiteDatabase) {
            try {
                val inputStream = context.resources.openRawResource(R.raw.join)
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.use { r ->
                    r.readLine() // Skip header
                    var line: String? = r.readLine()
                    while (line != null) {
                        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                        if (parts.size >= 6) {
                            val workoutIDStr = clean(parts[0]) // e.g. "W001"
                            val exerciseIDStr = clean(parts[1]) // e.g. "E001"
                            val sets = clean(parts[2]).toIntOrNull() ?: 0
                            val repsStr = clean(parts[3]) // e.g. "6-8" or "30 sec"
                            val rest = clean(parts[4]).toIntOrNull() ?: 0
                            val order = clean(parts[5]).toIntOrNull() ?: 0

                            // Convert W001 to 1, E001 to 1
                            val workoutID = workoutIDStr.substring(1).toIntOrNull() ?: 0
                            val exerciseID = exerciseIDStr.substring(1).toIntOrNull() ?: 0

                            // Parse reps: handle "6-8" or "30 sec"
                            // We'll take the first number for the Int column
                            val reps = repsStr.split("-", " ").firstOrNull()?.toIntOrNull() ?: 0

                            db.execSQL(
                                "INSERT INTO WorkEx (workoutName, workoutID, exerciseID, reps, sets, restTime, exOrder) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                arrayOf<Any>(workoutIDStr, workoutID, exerciseID, reps, sets, rest, order)
                            )
                        }
                        line = r.readLine()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun clean(part: String): String {
            var s = part.trim()
            if (s.startsWith("\"") && s.endsWith("\"")) {
                s = s.substring(1, s.length - 1).replace("\"\"", "\"")
            }
            return s
        }
    }
}

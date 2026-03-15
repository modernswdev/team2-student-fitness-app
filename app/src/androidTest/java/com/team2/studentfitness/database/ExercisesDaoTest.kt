package com.team2.studentfitness.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExercisesDaoTest {
    private lateinit var exercisesDao: ExercisesDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).addCallback(Database.getDatabaseCallback(context))
         .build()

        exercisesDao = db.exercisesDao()

        //Dummy query to ensure pre-population occurs before tests
        runBlocking {
            exercisesDao.getAll()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun checkPrepopulatedData() = runTest {
        val allExercises = exercisesDao.getAll()

        // exercise.csv has 80 exercises (E001 to E080)
        assertTrue("Database should be pre-populated with exercises", allExercises.isNotEmpty())
        assertEquals(80, allExercises.size)

        // Check first exercise's accuracy
        val firstResult = exercisesDao.getByName("Barbell Bench Press")
        assertNotNull(firstResult)
        assertEquals(1, firstResult.size)
        assertEquals("Chest", firstResult[0].muscleGroup)
        assertEquals(1, firstResult[0].difficulty) // Intermediate = 1

        // Check a beginner difficulty exercise
        val beginner = exercisesDao.getByName("Chest Fly (Dumbbell)")
        assertEquals(0, beginner[0].difficulty) // Beginner = 0

        // Check an advanced difficulty exercise
        val advanced = exercisesDao.getByName("Pull-Ups")
        assertEquals(2, advanced[0].difficulty) // Advanced = 2
    }

    @Test
    fun insertAndGetAllExercises() = runTest {
        val exercise = Exercises(
            uid = 1000, // High ID to avoid conflict
            name = "Custom Exercise",
            muscleGroup = "Arms",
            difficulty = 1,
            description = "Description"
        )
        exercisesDao.insert(exercise)
        val result = exercisesDao.getByName("Custom Exercise")
        assertEquals(1, result.size)
        assertEquals("Custom Exercise", result[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteExercises() {
        val e1 = Exercises(uid = 1, workoutName = "Plank")
        val e2 = Exercises(uid = 2, workoutName = "Lunge")
        exercisesDao.insert(e1)
        exercisesDao.insert(e2)

        exercisesDao.delete(e1)
        assertEquals(1, exercisesDao.getAll().size)

        exercisesDao.deleteById(2)
        assertTrue(exercisesDao.getAll().isEmpty())
    }
}

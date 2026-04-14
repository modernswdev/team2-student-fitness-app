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
class WorkoutsDaoTest {
    private lateinit var workoutsDao: WorkoutsDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Add the callback to trigger pre-population
        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).addCallback(Database.getDatabaseCallback(context))
         .fallbackToDestructiveMigration(true)
         .build()
        
        workoutsDao = db.workoutsDao()

        //Dummy query to ensure pre-population occurs before tests
        runBlocking {
            workoutsDao.getAll()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun printFirstWorkout() = runTest {
        val allWorkouts = workoutsDao.getAll()
        if (allWorkouts.isNotEmpty()) {
            val first = allWorkouts[0]
            println("First Workout ID: ${first.workoutID}, Name: ${first.workoutName}")
        }
    }

    @Test
    fun checkPrepopulatedData() = runTest {
        val allWorkouts = workoutsDao.getAll()
        
        // workouts.csv has 25 workouts (W001 to W025)
        assertTrue("Database should be pre-populated with workouts", allWorkouts.isNotEmpty())
        assertEquals(25, allWorkouts.size)
        
        // Check first workout (W001,Push Strength,50,Chest/Shoulders/Triceps,Strength,Intermediate,Push)
        val firstResult = workoutsDao.getByName("Push Strength")
        assertNotNull(firstResult)
        assertEquals(1, firstResult.size)
        assertEquals(1, firstResult[0].workoutID)
        assertEquals(50, firstResult[0].duration)
        assertEquals(2, firstResult[0].type) // Strength = 2
        assertEquals(1, firstResult[0].difficulty) // Intermediate = 1
        assertEquals("Push", firstResult[0].split)
        
        // Check a recovery workout
        val recovery = workoutsDao.getByName("Mobility & Recovery")
        assertEquals(0, recovery[0].type) // Recovery = 0
        assertEquals(0, recovery[0].difficulty) // Beginner = 0
        
        // Check a cardio workout
        val hiit = workoutsDao.getByName("HIIT 30")
        assertEquals(1, hiit[0].type) // Cardio = 1
    }

    @Test
    fun insertAndGetAllWorkouts() = runTest {
        val workout = Workouts(
            workoutID = 1000, // High ID to avoid conflict with pre-populated data
            workoutName = "Custom Workout",
            duration = 30,
            focus = "Flexibility",
            type = 0,
            difficulty = 0,
            split = "Full Body"
        )
        workoutsDao.insert(workout)
        val result = workoutsDao.getByName("Custom Workout")
        assertEquals(1, result.size)
        assertEquals("Custom Workout", result[0].workoutName)
    }

    @Test
    fun filterQueries() = runTest {
        // Test filtering on pre-populated data
        val strengthWorkouts = workoutsDao.getByType(2) // Strength
        assertTrue("Strength workouts should exist", strengthWorkouts.isNotEmpty())
        
        val intermediateWorkouts = workoutsDao.getByDifficulty(1) // Intermediate
        assertTrue("Intermediate workouts should exist", intermediateWorkouts.isNotEmpty())
    }

    @Test
    fun getIndividualFields() = runTest {
        // W001 has duration 50
        val first = workoutsDao.getByName("Push Strength")[0]
        val id = first.workoutID
        
        assertEquals(first.type, workoutsDao.getType(id))
        assertEquals(50, workoutsDao.getDuration(id))
        assertEquals("Push", workoutsDao.getSplit(id))
    }

    @Test
    fun deleteWorkouts() = runTest {
        val allBefore = workoutsDao.getAll()
        val initialSize = allBefore.size
        
        if (allBefore.isNotEmpty()) {
            workoutsDao.delete(allBefore[0])
            assertEquals(initialSize - 1, workoutsDao.getAll().size)
        }
    }
}

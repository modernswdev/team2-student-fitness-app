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
class WorkExDaoTest {
    private lateinit var workExDao: WorkExDao
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
        
        workExDao = db.workExDao()

        //Dummy query to ensure pre-population occurs before tests
        runBlocking {
            workExDao.getAll()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertOrderedWorkExTest() = runTest {
        val workoutID = 2000
        val workoutName = "Test"
        
        val inputs = listOf(
            WorkExInput(
                workoutName = workoutName,
                workoutID = workoutID,
                exerciseID = 1,
                reps = 1,
                sets = 2,
                restTime = 40
            ),
            WorkExInput(
                workoutName = workoutName,
                workoutID = workoutID,
                exerciseID = 2,
                reps = 3,
                sets = 3,
                restTime = 10
            )
        )
        
        workExDao.insertOrderedWorkEx(inputs)
        
        val results = workExDao.getByName(workoutName)
        assertEquals(2, results.size)
        
        // Verify first exercise
        val first = results.find { it.order == 1 }
        assertNotNull(first)
        assertEquals(workoutID, first?.workoutID)
        assertEquals(1, first?.exerciseID)
        assertEquals(1, first?.reps)
        assertEquals(2, first?.sets)
        assertEquals(40, first?.restTime)
        
        // Verify second exercise
        val second = results.find { it.order == 2 }
        assertNotNull(second)
        assertEquals(workoutID, second?.workoutID)
        assertEquals(2, second?.exerciseID)
        assertEquals(3, second?.reps)
        assertEquals(3, second?.sets)
        assertEquals(10, second?.restTime)
    }

    @Test
    fun checkPrepopulatedData() = runTest {
        val allWorkEx = workExDao.getAll()
        
        // join.csv has 124 records
        assertEquals(124, allWorkEx.size)
        
        // Check first record
        val w001Records = workExDao.getByName("W001")
        assertTrue(w001Records.isNotEmpty())
        val first = w001Records.find { it.order == 1 }
        assertNotNull(first)
        assertEquals(1, first?.workoutID) // W001 -> 1
        assertEquals(1, first?.exerciseID) // E001 -> 1
        assertEquals(4, first?.sets)
        assertEquals(6, first?.reps) // "6-8" parsed as 6
        assertEquals(120, first?.restTime)
    }

    @Test
    fun insertAndGetAllWorkEx() = runTest {
        val workEx = WorkEx(
            uid = 0,
            workoutID = 1000,
            workoutName = "Custom",
            exerciseID = 200,
            reps = 10,
            sets = 3,
            restTime = 60,
            order = 1
        )
        workExDao.insert(workEx)
        val result = workExDao.getByName("Custom")
        assertEquals(1, result.size)
        assertEquals(1000, result[0].workoutID)
    }

    @Test
    fun updateMethods() = runTest {
        // Get an existing record to update
        val first = workExDao.getAll()[0]
        val uid = first.uid

        workExDao.editReps(uid, 99)
        assertEquals(99, workExDao.getById(uid).reps)

        workExDao.editSets(uid, 88)
        assertEquals(88, workExDao.getById(uid).sets)

        workExDao.editRestTime(uid, 77)
        assertEquals(77, workExDao.getById(uid).restTime)

        workExDao.editOrder(uid, 66)
        assertEquals(66, workExDao.getById(uid).order)
    }

    @Test
    fun deleteWorkEx() = runTest {
        val allBefore = workExDao.getAll()
        val initialSize = allBefore.size
        
        if (allBefore.isNotEmpty()) {
            workExDao.delete(allBefore[0])
            assertEquals(initialSize - 1, workExDao.getAll().size)
        }
    }
}

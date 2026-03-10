package com.team2.studentfitness.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).allowMainThreadQueries().build()
        workExDao = db.workExDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllWorkEx() {
        val workEx = WorkEx(
            uid = 1,
            workoutName = "Morning Routine",
            userID = 101,
            exerciseID = 201,
            reps = 10,
            sets = 3,
            restTime = 60,
            order = 1
        )
        workExDao.insert(workEx)
        val allWorkEx = workExDao.getAll()
        assertEquals(1, allWorkEx.size)
        assertEquals("Morning Routine", allWorkEx[0].workoutName)
    }

    @Test
    @Throws(Exception::class)
    fun getByIdAndName() {
        val workEx = WorkEx(1, "Bench Press Session", 101, 202, 8, 4, 90, 2)
        workExDao.insert(workEx)

        val byId = workExDao.getById(1)
        assertNotNull(byId)
        assertEquals("Bench Press Session", byId.workoutName)

        val byName = workExDao.getByName("Bench Press Session")
        assertEquals(1, byName.size)
        assertEquals(1, byName[0].uid)
    }

    @Test
    @Throws(Exception::class)
    fun updateMethods() {
        val workEx = WorkEx(1, "Test Workout", 1, 1, 10, 3, 60, 1)
        workExDao.insert(workEx)

        workExDao.editReps(1, 12)
        assertEquals(12, workExDao.getById(1).reps)

        workExDao.editSets(1, 5)
        assertEquals(5, workExDao.getById(1).sets)

        workExDao.editRestTime(1, 45)
        assertEquals(45, workExDao.getById(1).restTime)

        workExDao.editOrder(1, 2)
        assertEquals(2, workExDao.getById(1).order)
    }

    @Test
    @Throws(Exception::class)
    fun deleteWorkEx() {
        val we1 = WorkEx(1, "W1", 1, 1, 10, 3, 60, 1)
        val we2 = WorkEx(2, "W2", 1, 2, 10, 3, 60, 2)
        workExDao.insert(we1)
        workExDao.insert(we2)

        workExDao.delete(we1)
        assertEquals(1, workExDao.getAll().size)

        workExDao.deleteById(2)
        assertTrue(workExDao.getAll().isEmpty())
    }
}

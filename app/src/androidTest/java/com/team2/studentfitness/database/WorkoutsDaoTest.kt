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
class WorkoutsDaoTest {
    private lateinit var workoutsDao: WorkoutsDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).allowMainThreadQueries().build()
        workoutsDao = db.workoutsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllWorkouts() {
        val workout = Workouts(
            uid = 1,
            name = "Morning Yoga",
            duration = 30,
            focus = "Flexibility",
            type = 0,
            difficulty = 0,
            split = "Full Body"
        )
        workoutsDao.insert(workout)
        val allWorkouts = workoutsDao.getAll()
        assertEquals(1, allWorkouts.size)
        assertEquals("Morning Yoga", allWorkouts[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun getByIdAndName() {
        val workout = Workouts(1, "Running", 45, "Cardio", 1, 1, "Legs")
        workoutsDao.insert(workout)

        val byId = workoutsDao.getById(1)
        assertNotNull(byId)
        assertEquals("Running", byId.name)

        val byName = workoutsDao.getByName("Running")
        assertEquals(1, byName.size)
        assertEquals(1, byName[0].uid)
    }

    @Test
    @Throws(Exception::class)
    fun filterQueries() {
        val w1 = Workouts(1, "W1", 20, "F1", 0, 0, "S1")
        val w2 = Workouts(2, "W2", 40, "F2", 1, 1, "S2")
        workoutsDao.insert(w1)
        workoutsDao.insert(w2)

        assertEquals(1, workoutsDao.getByType(0).size)
        assertEquals(1, workoutsDao.getByDuration(40).size)
        assertEquals(1, workoutsDao.getByFocus("F1").size)
        assertEquals(1, workoutsDao.getByDifficulty(1).size)
        assertEquals(1, workoutsDao.getBySplit("S2").size)
    }

    @Test
    @Throws(Exception::class)
    fun getIndividualFields() {
        val workout = Workouts(1, "Strength", 60, "Upper", 2, 2, "Push")
        workoutsDao.insert(workout)

        assertEquals(2, workoutsDao.getType(1))
        assertEquals(60, workoutsDao.getDuration(1))
        assertEquals("Upper", workoutsDao.getFocus(1))
        assertEquals(2, workoutsDao.getDifficulty(1))
        assertEquals("Push", workoutsDao.getSplit(1))
    }

    @Test
    @Throws(Exception::class)
    fun deleteWorkouts() {
        val w1 = Workouts(1, "W1", 20, "F1", 0, 0, "S1")
        val w2 = Workouts(2, "W2", 40, "F2", 1, 1, "S2")
        workoutsDao.insert(w1)
        workoutsDao.insert(w2)

        workoutsDao.delete(w1)
        assertEquals(1, workoutsDao.getAll().size)

        workoutsDao.deleteById(2)
        assertTrue(workoutsDao.getAll().isEmpty())
    }
}

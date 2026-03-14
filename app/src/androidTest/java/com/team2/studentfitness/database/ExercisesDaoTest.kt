package com.team2.studentfitness.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        ).allowMainThreadQueries().build()
        exercisesDao = db.exercisesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetAllExercises() = runTest {
        val exercise = Exercises(
            uid = 1,
            workoutName = "Pushups",
            muscleGroup = "Chest",
            difficulty = 1,
            description = "A standard pushup exercise targeting chest and triceps."
        )
        exercisesDao.insert(exercise)
        val allExercises = exercisesDao.getAll()
        assertEquals(1, allExercises.size)
        assertEquals("Pushups", allExercises[0].workoutName)
        assertEquals("Chest", allExercises[0].muscleGroup)
        assertEquals(1, allExercises[0].difficulty)
        assertEquals("A standard pushup exercise targeting chest and triceps.", allExercises[0].description)
    }

    @Test
    fun getByIdAndName() = runTest {
        val exercise = Exercises(
            uid = 1,
            workoutName = "Squats",
            muscleGroup = "Legs",
            difficulty = 1,
            description = "Basic bodyweight squats."
        )
        exercisesDao.insert(exercise)

        val byId = exercisesDao.getById(1)
        assertNotNull(byId)
        assertEquals("Squats", byId.workoutName)

        val byName = exercisesDao.getByName("Squats")
        assertEquals(1, byName.size)
        assertEquals(1, byName[0].uid)
    }

    @Test
    fun testFilters() = runTest {
        val e1 = Exercises(uid = 1, workoutName = "Plank", muscleGroup = "Core", difficulty = 0, description = "Static hold.")
        val e2 = Exercises(uid = 2, workoutName = "Deadlift", muscleGroup = "Back", difficulty = 2, description = "Compound pull.")
        exercisesDao.insert(e1)
        exercisesDao.insert(e2)

        val coreExercises = exercisesDao.getByMuscleGroup("Core")
        assertEquals(1, coreExercises.size)
        assertEquals("Plank", coreExercises[0].workoutName)

        val advancedExercises = exercisesDao.getByDifficulty(2)
        assertEquals(1, advancedExercises.size)
        assertEquals("Deadlift", advancedExercises[0].workoutName)
    }

    @Test
    fun deleteExercises() = runTest {
        val e1 = Exercises(uid = 1, workoutName = "Plank", muscleGroup = "Body", difficulty = 1, description = "Desc")
        val e2 = Exercises(uid = 2, workoutName = "Lunge", muscleGroup = "Legs", difficulty = 1, description = "Desc")
        exercisesDao.insert(e1)
        exercisesDao.insert(e2)

        exercisesDao.delete(e1)
        assertEquals(1, exercisesDao.getAll().size)

        exercisesDao.deleteById(2)
        assertTrue(exercisesDao.getAll().isEmpty())
    }
}

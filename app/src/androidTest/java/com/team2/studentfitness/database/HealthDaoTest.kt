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
class HealthDaoTest {
    private lateinit var healthDao: HealthDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).allowMainThreadQueries().build()
        healthDao = db.healthDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetHealthData() {
        val healthData = HealthData(
            id = 1,
            timestamp = "2023-10-27 10:00:00",
            heartRate = 72,
            bodyTemp = 37,
            totalSteps = 5000,
            stepCount = 1000
        )
        healthDao.insert(healthData)
        val result = healthDao.getById(1)
        assertNotNull(result)
        assertEquals(72, result.heartRate)
        assertEquals("2023-10-27 10:00:00", result.timestamp)
    }

    @Test
    @Throws(Exception::class)
    fun getHeartRateAndBodyTemp() {
        val timestamp = "2023-10-27 12:00:00"
        val healthData = HealthData(1, timestamp, 80, 36, 0, 0)
        healthDao.insert(healthData)

        val hr = healthDao.getHeartRate(timestamp)
        val temp = healthDao.getBodyTemp(timestamp)
        
        assertEquals(80, hr)
        assertEquals(36, temp)
    }

    @Test
    @Throws(Exception::class)
    fun stepCountOperations() {
        val healthData = HealthData(1, "2023-10-27 14:00:00", 70, 37, 1000, 500)
        healthDao.insert(healthData)

        // Update current steps
        healthDao.updateCurrentSteps(100, 1)
        assertEquals(600, healthDao.getStepCount(1))

        // Reset step count
        healthDao.resetStepCount(1)
        assertEquals(0, healthDao.getStepCount(1))
    }

    @Test
    @Throws(Exception::class)
    fun totalStepsOperations() {
        val healthData = HealthData(1, "2023-10-27 14:00:00", 70, 37, 1000, 500)
        healthDao.insert(healthData)

        // Update total steps
        healthDao.updateTotalSteps(200, 1)
        assertEquals(1200, healthDao.getTotalSteps(1))

        // Add current steps to total
        healthDao.addCurrentSteps(1) // 1200 + 500
        assertEquals(1700, healthDao.getTotalSteps(1))
    }

    @Test
    @Throws(Exception::class)
    fun deleteHealthData() {
        val healthData = HealthData(1, "2023-10-27 15:00:00", 75, 37, 0, 0)
        healthDao.insert(healthData)
        healthDao.deleteById(1)
        
        val allData = healthDao.getAll()
        assertTrue(allData.isEmpty())
    }
}

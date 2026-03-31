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
class SettingsDaoTest {
    private lateinit var settingsDao: SettingsDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java
        ).allowMainThreadQueries().build()
        settingsDao = db.settingsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetSettings() = runTest {
        val settings = UserSettings(
            uid = 1,
            name = "John Doe",
            notifsOn = true,
            theme = 1,
            homeGym = 0,
            loginCount = 5,
            isMetric = true,
            sex = "MALE",
            activityLevel = "MODERATE"
        )
        settingsDao.insert(settings)
        val result = settingsDao.getById(1)
        assertNotNull(result)
        assertEquals("John Doe", result.name)
        assertEquals(true, result.notifsOn)
        assertEquals(1, result.theme)
        assertEquals(true, result.isMetric)
        assertEquals("MALE", result.sex)
        assertEquals("MODERATE", result.activityLevel)
    }

    @Test
    fun updateDarkMode() = runTest {
        val settings = UserSettings(1, "User", true, 0, 0, 0)
        settingsDao.insert(settings)
        
        settingsDao.updateDarkMode(1, 1)
        val theme = settingsDao.getTheme(1)
        assertEquals(1, theme)
    }

    @Test
    fun updateIsMetric() = runTest {
        val settings = UserSettings(1, "User", true, 1, 0, 0, isMetric = true)
        settingsDao.insert(settings)
        
        settingsDao.updateIsMetric(false, 1)
        val result = settingsDao.getById(1)
        assertEquals(false, result.isMetric)
    }

    @Test
    fun updateHomeGym() = runTest {
        val settings = UserSettings(1, "User", true, 1, 0, 0)
        settingsDao.insert(settings)
        
        settingsDao.updateHomeGym(2, 1)
        val result = settingsDao.getById(1)
        assertEquals(2, result.homeGym)
    }

    @Test
    fun updateNotifs() = runTest {
        val settings = UserSettings(1, "User", true, 1, 0, 0)
        settingsDao.insert(settings)
        
        settingsDao.updateNotifs(false, 1)
        val notifs = settingsDao.getNotifs(1)
        assertEquals(false, notifs)
    }

    @Test
    fun deleteSettings() = runTest {
        val settings = UserSettings(1, "User", true, 1, 0, 0)
        settingsDao.insert(settings)
        settingsDao.delete(settings)
        
        val allSettings = settingsDao.getAll()
        assertTrue(allSettings.isEmpty())
    }
}

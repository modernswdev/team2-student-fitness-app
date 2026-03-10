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
    @Throws(Exception::class)
    fun insertAndGetSettings() {
        val settings = UserSettings(
            uid = 1,
            name = "John Doe",
            notifsOn = true,
            theme = 1,
            homeGym = 101,
            loginCount = 5
        )
        settingsDao.insert(settings)
        val result = settingsDao.getById(1)
        assertNotNull(result)
        assertEquals("John Doe", result.name)
        assertEquals(true, result.notifsOn)
        assertEquals(1, result.theme)
        assertEquals(5, result.loginCount)
    }

    @Test
    @Throws(Exception::class)
    fun updateTheme() {
        val settings = UserSettings(1, "User", true, 1, 101, 0)
        settingsDao.insert(settings)
        
        settingsDao.updateTheme(2, 1)
        val theme = settingsDao.getTheme(1)
        assertEquals(2, theme)
    }

    @Test
    @Throws(Exception::class)
    fun updateNotifs() {
        val settings = UserSettings(1, "User", true, 1, 101, 0)
        settingsDao.insert(settings)
        
        settingsDao.updateNotifs(false, 1)
        val notifs = settingsDao.getNotifs(1)
        assertEquals(false, notifs)
    }

    @Test
    @Throws(Exception::class)
    fun incrementLoginCount() {
        val settings = UserSettings(1, "User", true, 1, 101, 10)
        settingsDao.insert(settings)
        
        settingsDao.incrementLoginCount(1)
        val loginCount = settingsDao.getLoginCount(1)
        assertEquals(11, loginCount)
    }

    @Test
    @Throws(Exception::class)
    fun deleteSettings() {
        val settings = UserSettings(1, "User", true, 1, 101, 0)
        settingsDao.insert(settings)
        settingsDao.delete(settings)
        
        val allSettings = settingsDao.getAll()
        assertTrue(allSettings.isEmpty())
    }
}

package com.team2.studentfitness.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.team2.studentfitness.database.databaseTest.User
import com.team2.studentfitness.database.databaseTest.UserDao
import com.team2.studentfitness.database.databaseTest.UserDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {
    private var _userDao: UserDao? = null
    private val userDao get() = _userDao!!
    
    private var _db: UserDatabase? = null
    private val db get() = _db!!

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        try {
            _db = Room.inMemoryDatabaseBuilder(
                context, UserDatabase::class.java
            ).allowMainThreadQueries().build()
            _userDao = _db?.userDao()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        _db?.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        // Use uid = 0 to allow Room to auto-generate the ID
        val user = User(uid = 0, name = "TestUser", password = "password123", loginCount = 0)
        userDao.insert(user)
        val byName = userDao.findByName("TestUser")
        assertNotNull("User should be found by name", byName)
        assertEquals("TestUser", byName?.name)
    }

    @Test
    @Throws(Exception::class)
    fun incrementLoginCount() {
        val user = User(uid = 1, name = "TestUser", password = "password123", loginCount = 0)
        userDao.insert(user)
        userDao.incrementLoginCount(1)
        val updatedUser = userDao.getUserById(1)
        assertNotNull("User should exist", updatedUser)
        assertEquals(1, updatedUser?.loginCount)
    }

    @Test
    @Throws(Exception::class)
    fun deleteUser() {
        val user = User(uid = 1, name = "TestUser", password = "password123", loginCount = 0)
        userDao.insert(user)
        userDao.delete(user)
        val allUsers = userDao.getAll()
        assertEquals(0, allUsers.size)
    }
}

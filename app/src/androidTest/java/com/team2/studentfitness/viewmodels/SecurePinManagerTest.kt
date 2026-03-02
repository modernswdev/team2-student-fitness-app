package com.team2.studentfitness.viewmodels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class SecurePinManagerTest {
    private lateinit var pinManager: SecurePinManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        pinManager = SecurePinManager(context)
        pinManager.clearPin() // Clean state before each test
    }

    @Test
    fun testSetAndVerifyPin() {
        pinManager.setPin(1234)
        assertTrue(pinManager.verifyPin(1234))
    }

    @Test
    fun testVerifyWrongPin() {
        pinManager.setPin(1234)
        assertFalse(pinManager.verifyPin(5678))
    }

    @Test
    fun testIsPinSet() {
        assertFalse(pinManager.isPinSet())
        pinManager.setPin(1234)
        assertTrue(pinManager.isPinSet())
    }

    @Test
    fun testClearPin() {
        pinManager.setPin(1234)
        pinManager.clearPin()
        assertFalse(pinManager.isPinSet())
    }
}
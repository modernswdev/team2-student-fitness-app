package com.team2.studentfitness.viewmodels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var pinManager: SecurePinManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        pinManager = SecurePinManager(context)
        pinManager.clearPin()
        loginViewModel = LoginViewModel(pinManager)
    }

    @Test
    fun testSetAndUnlockPin() {
        assertFalse("PIN should not be set initially", loginViewModel.isPinSet())
        
        loginViewModel.setPin(1234)
        assertTrue("PIN should be set after calling setPin", loginViewModel.isPinSet())
        
        assertTrue("Correct PIN should unlock", loginViewModel.pinUnlock(1234))
        assertFalse("Incorrect PIN should not unlock", loginViewModel.pinUnlock(5678))
    }

    @Test
    fun testResetPin() {
        loginViewModel.setPin(1111)
        
        // Reset with correct old PIN
        loginViewModel.resetPin(1111, 2222)
        assertTrue("New PIN should unlock after successful reset", loginViewModel.pinUnlock(2222))
        assertFalse("Old PIN should no longer unlock", loginViewModel.pinUnlock(1111))
        
        // Try reset with wrong old PIN
        loginViewModel.resetPin(9999, 3333)
        assertFalse("PIN should not have been updated with wrong old PIN", loginViewModel.pinUnlock(3333))
        assertTrue("Current PIN should still be active", loginViewModel.pinUnlock(2222))
    }

    @Test
    fun testClearPin() {
        loginViewModel.setPin(1234)
        loginViewModel.clearPin()
        assertFalse("PIN should be cleared", loginViewModel.isPinSet())
    }
}

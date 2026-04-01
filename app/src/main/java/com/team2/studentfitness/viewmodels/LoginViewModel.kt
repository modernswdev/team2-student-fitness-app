package com.team2.studentfitness.viewmodels

import com.team2.studentfitness.database.UserDatabase


class LoginViewModel(private val pinManager: SecurePinManager){
    // Login is now handled via PIN
    fun pinUnlock(pin: Int): Boolean {
        return pinManager.verifyPin(pin)
    }

    fun setPin(newPin: Int) {
        pinManager.setPin(newPin)
    }

    fun resetPin(oldPin: Int, newPin: Int) {
        if (pinManager.verifyPin(oldPin)) {
            pinManager.setPin(newPin)
        }
    }

    fun isPinSet(): Boolean {
        return pinManager.isPinSet()
    }

    fun clearPin() {
        pinManager.clearPin()
    }
}
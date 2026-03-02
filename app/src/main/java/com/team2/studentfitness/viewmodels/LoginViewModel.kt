package com.team2.studentfitness.viewmodels

class LoginViewModel(private val pinManager: SecurePinManager){
    //This is currently unused until we implement accounts, if we do so.
    fun login(username: String, password: String): Boolean {
        //TODO: Implement actual login logic, this is just a placeholder
        if (username == "user" && password == "password") {
            return true
        }
        return false
    }

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
package com.team2.studentfitness.viewmodels

class LoginViewModel {
    //This is currently unused until we implement accounts, if we do so.
    fun login(username: String, password: String): Boolean {
        //TODO: Implement actual login logic, this is just a placeholder
        if (username == "user" && password == "password") {
            return true
        }
        return false
    }
    fun pinUnlock(pin: Int) : Boolean {
        //TODO: Implement actual login logic, this is just a placeholder
        if (pin == 1234) {
            return true
        }
        return false
    }
    fun setPin(newPin: Int) {
        //TODO Implement actual logic to set the pin, this is just a placeholder
    }
    fun resetPin(oldPin: Int, newPin: Int) {
        //TODO Implement actual logic to reset the pin, this is just a placeholder
    }
}
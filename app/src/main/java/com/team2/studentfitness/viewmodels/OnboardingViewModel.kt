package com.team2.studentfitness.viewmodels

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team2.studentfitness.database.Database
import com.team2.studentfitness.database.HealthData
import com.team2.studentfitness.database.User
import com.team2.studentfitness.database.UserDatabase
import com.team2.studentfitness.database.UserSettings
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application, private val securePinManager: SecurePinManager) : AndroidViewModel(application) {
    private val appDatabase = Database.getDatabase(application)
    private val userDatabase = UserDatabase.getDatabase(application)
    
    private val sharedPrefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun completeOnboarding(
        name: String,
        age: Int,
        weight: Float,
        height: Float,
        pin: Int?,
        isMetric: Boolean,
        sex: String,
        activityLevel: String,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Store Account Data (No password as requested)
                val user = User(name = name, loginCount = 1)
                val userId = userDatabase.userDao().insert(user)
                val uid = userId.toInt()

                // Convert to metric if imperial was chosen
                val finalWeight = if (isMetric) weight else weight * 0.453592f
                val finalHeight = if (isMetric) height else height * 2.54f

                // 2. Store Health/Biometric Data (Age, Weight, Height)
                val healthData = HealthData(
                    uid = uid,
                    heartRate = 0,
                    bodyTemp = 0,
                    totalSteps = 0,
                    stepCount = 0,
                    age = age,
                    weight = finalWeight,
                    height = finalHeight
                )
                appDatabase.healthDao().insert(healthData)

                // 3. Store User Settings - Default theme to 0 (Light Mode)
                val userSettings = UserSettings(
                    uid = uid,
                    name = name,
                    notifsOn = true,
                    theme = 0,
                    homeGym = 0,
                    loginCount = 1,
                    isMetric = isMetric,
                    sex = sex,
                    activityLevel = activityLevel
                )
                appDatabase.settingsDao().insert(userSettings)

                // 4. Handle PIN
                if (pin != null && isValidPin(pin)) {
                    securePinManager.setPin(pin)
                } else {
                    // Clear any existing PIN if the new one is null or invalid
                    securePinManager.clearPin()
                }

                // 5. Mark Onboarding as Done
                sharedPrefs.edit {
                    putBoolean("onboarding_completed", true)
                }

                onComplete()
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

    private fun isValidPin(pin: Int): Boolean {
        // Basic validation: 4-digit PIN, non-negative, non-trivial pattern
        if (pin < 0 || pin > 9999) return false

        val pinString = String.format("%04d", pin)

        // Reject pins where all digits are the same (e.g., 0000, 1111)
        if (pinString.all { it == pinString[0] }) return false

        // Reject simple ascending sequences (e.g., 0123, 1234)
        val isAscending = pinString
            .zipWithNext()
            .all { (a, b) -> b == a + 1 }
        if (isAscending) return false

        // Reject simple descending sequences (e.g., 4321, 3210)
        val isDescending = pinString
            .zipWithNext()
            .all { (a, b) -> b == a - 1 }
        if (isDescending) return false

        return true
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPrefs.getBoolean("onboarding_completed", false)
    }
}

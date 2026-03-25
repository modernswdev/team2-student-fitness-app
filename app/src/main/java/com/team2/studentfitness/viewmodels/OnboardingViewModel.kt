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
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            // 1. Store Account Data (No password as requested)
            val user = User(uid = 0, name = name, loginCount = 1)
            userDatabase.userDao().insert(user)
            val insertedUser = userDatabase.userDao().findByName(name)
            val uid = insertedUser?.uid ?: 0

            // 2. Store Health/Biometric Data (Age, Weight, Height)
            val healthData = HealthData(
                heartRate = 0,
                bodyTemp = 0,
                totalSteps = 0,
                stepCount = 0,
                age = age,
                weight = weight,
                height = height
            )
            appDatabase.healthDao().insert(healthData)

            // 3. Store User Settings
            val userSettings = UserSettings(
                uid = uid,
                name = name,
                notifsOn = true,
                theme = 1,
                homeGym = 0,
                loginCount = 1
            )
            appDatabase.settingsDao().insert(userSettings)

            // 4. Handle PIN
            if (pin != null) {
                securePinManager.setPin(pin)
            } else {
                securePinManager.clearPin()
            }

            // 5. Mark Onboarding as Done
            sharedPrefs.edit {
                putBoolean("onboarding_completed", true)
            }
            
            onComplete()
        }
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPrefs.getBoolean("onboarding_completed", false)
    }
}

package com.team2.studentfitness.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePinManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_pin_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setPin(pin: Int) {
        encryptedPrefs.edit().putInt("app_pin", pin).apply()
    }

    fun verifyPin(pin: Int): Boolean {
        val storedPin = encryptedPrefs.getInt("app_pin", -1)
        return pin == storedPin
    }

    fun isPinSet(): Boolean {
        return encryptedPrefs.getInt("app_pin", -1) != -1
    }

    fun clearPin() {
        encryptedPrefs.edit().remove("app_pin").apply()
    }
}

package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM usersettings")
    suspend fun getAll(): List<UserSettings>

    @Query("SELECT * FROM usersettings WHERE uid = :uid")
    suspend fun getById(uid: Int): UserSettings

    @Query("SELECT * FROM usersettings ORDER BY uid DESC LIMIT 1")
    suspend fun getLatest(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSettings: UserSettings)

    @Delete()
    suspend fun delete(userSettings: UserSettings)

    @Query("SELECT name FROM usersettings WHERE uid = :uid")
    suspend fun getName(uid: Int): String

    //Check current theme
    @Query("SELECT theme FROM usersettings WHERE uid = :uid")
    suspend fun getTheme(uid: Int): Int

    //Update theme
    @Query("UPDATE usersettings SET theme = :newTheme WHERE uid = :uid")
    suspend fun updateTheme(newTheme: Int, uid: Int)

    //Check if notifs are on
    @Query("SELECT notifsOn FROM usersettings WHERE uid = :uid")
    suspend fun getNotifs(uid: Int): Boolean

    //Change notifs on/off
    @Query("UPDATE usersettings SET notifsOn = :newNotifs WHERE uid = :uid")
    suspend fun updateNotifs(newNotifs: Boolean, uid: Int)

    @Query("UPDATE usersettings SET isMetric = :isMetric WHERE uid = :uid")
    suspend fun updateIsMetric(isMetric: Boolean, uid: Int)

    @Query("UPDATE usersettings SET homeGym = :homeGym WHERE uid = :uid")
    suspend fun updateHomeGym(homeGym: Int, uid: Int)

    // Using theme field for dark mode: 0 for light, 1 for dark
    @Query("UPDATE usersettings SET theme = :isDarkMode WHERE uid = :uid")
    suspend fun updateDarkMode(isDarkMode: Int, uid: Int)
}

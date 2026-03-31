package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM usersettings")
    suspend fun getAll(): List<UserSettings>

    @Query("SELECT * FROM usersettings WHERE uid = :uid")
    suspend fun getById(uid: Int): UserSettings

    @Query("SELECT * FROM usersettings ORDER BY uid DESC LIMIT 1")
    suspend fun getLatest(): UserSettings?

    @Insert()
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

    //Get current login count
    @Query("SELECT loginCount FROM usersettings WHERE uid = :uid")
    suspend fun getLoginCount(uid: Int): Int

    //Set login count to specific value
    @Query("UPDATE usersettings SET loginCount = :newLoginCount WHERE uid = :uid")
    suspend fun updateLoginCount(newLoginCount: Int, uid: Int)

    //Increment login count by 1
    @Query("UPDATE usersettings SET loginCount = loginCount + 1 WHERE uid = :uid")
    suspend fun incrementLoginCount(uid: Int)

    @Query("UPDATE usersettings SET isMetric = :isMetric WHERE uid = :uid")
    suspend fun updateIsMetric(isMetric: Boolean, uid: Int)
}

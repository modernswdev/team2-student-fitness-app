package com.team2.studentfitness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingsDao {
    @Query("SELECT * FROM usersettings")
    fun getAll(): List<UserSettings>

    @Query("SELECT * FROM usersettings WHERE uid = :uid")
    fun getById(uid: Int): UserSettings

    @Insert()
    fun insert(userSettings: UserSettings)

    @Delete()
    fun delete(userSettings: UserSettings)

    //Check current theme
    @Query("SELECT theme FROM usersettings WHERE uid = :uid")
    fun getTheme(uid: Int): Int

    //Update theme
    @Query("UPDATE usersettings SET theme = :newTheme WHERE uid = :uid")
    fun updateTheme(newTheme: Int, uid: Int)

    //Check if notifs are on
    @Query("SELECT notifsOn FROM usersettings WHERE uid = :uid")
    fun getNotifs(uid: Int): Boolean

    //Change notifs on/off
    @Query("UPDATE usersettings SET notifsOn = :newNotifs WHERE uid = :uid")
    fun updateNotifs(newNotifs: Boolean, uid: Int)

    //Get current login count
    @Query("SELECT loginCount FROM usersettings WHERE uid = :uid")
    fun getLoginCount(uid: Int): Int

    //Set login count to specific value
    @Query("UPDATE usersettings SET loginCount = :newLoginCount WHERE uid = :uid")
    fun updateLoginCount(newLoginCount: Int, uid: Int)

    //Increment login count by 1
    @Query("UPDATE usersettings SET loginCount = loginCount + 1 WHERE uid = :uid")
    fun incrementLoginCount(uid: Int)
}
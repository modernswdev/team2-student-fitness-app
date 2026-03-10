package com.team2.studentfitness.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//This table shows all user settings
@Entity
data class UserSettings (
    @PrimaryKey //Can use the user ID as primary key
    val uid: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "notifsOn")
    val notifsOn: Boolean,
    @ColumnInfo(name = "theme", defaultValue = "1")
    val theme: Int,
    @ColumnInfo(name = "homeGym")
    val homeGym: Int,
    @ColumnInfo(name = "loginCount")
    val loginCount: Int
)


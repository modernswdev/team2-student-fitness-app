package com.team2.studentfitness.database.databaseTest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//This entity is for testing purposes only
@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "loginCount")
    val loginCount: Int
)


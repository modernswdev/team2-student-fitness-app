package com.team2.studentfitness.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//This table shows all exercises
@Entity
data class Workouts (
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "duration")
    val duration: Int,
    @ColumnInfo(name = "focus")
    val focus: String,
    @ColumnInfo(name = "type")
    val type: Int,   //0 = recovery, 1 = cardio, 2 = strength, 3 = mixed
    @ColumnInfo(name = "difficulty")
    val difficulty: Int,   //0 = beginner, 1 = intermediate, 2 = advanced
    @ColumnInfo(name = "split")
    val split: String
)


package com.team2.studentfitness.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//This table shows all exercises
@Entity
data class Exercises (
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo(name = "name")
    val workoutName: String,
    @ColumnInfo(name = "muscleGroup")
    val muscleGroup: String,
    @ColumnInfo(name = "difficulty")
    val difficulty: Int,   //0 = beginner, 1 = intermediate, 2 = advanced
    @ColumnInfo(name = "description")
    val description: String
)


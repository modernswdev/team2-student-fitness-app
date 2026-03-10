package com.team2.studentfitness.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//This table lists all workouts with their exercises, with each combination of workout and exercise having a unique ID
@Entity
data class WorkEx (
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo(name = "workoutName")
    val workoutName: String,
    @ColumnInfo(name = "workoutID")
    val userID: Int,
    @ColumnInfo(name = "exerciseID")
    val exerciseID: Int,
    @ColumnInfo(name = "reps")
    val reps: Int,
    @ColumnInfo(name = "sets")
    val sets: Int,
    @ColumnInfo(name = "restTime")
    val restTime: Int,
    @ColumnInfo(name = "exOrder")
    val order: Int
    )


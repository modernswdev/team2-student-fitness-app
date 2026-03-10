package com.team2.studentfitness.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//This table records user health data
@Entity(tableName = "healthdata")
data class HealthData (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    val timestamp: String? = null,
    @ColumnInfo(name = "heartRate")
    val heartRate: Int,
    @ColumnInfo(name = "bodyTemp")
    val bodyTemp: Int,
    @ColumnInfo(name = "totalSteps")
    val totalSteps: Int,
    @ColumnInfo(name = "stepCount")
    val stepCount: Int
)

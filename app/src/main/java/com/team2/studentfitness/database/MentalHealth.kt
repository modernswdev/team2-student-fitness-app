package com.team2.studentfitness.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mental_health")
data class MentalHealth(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood: String,
    val reflection: String,
    val timestamp: Long = System.currentTimeMillis()
)

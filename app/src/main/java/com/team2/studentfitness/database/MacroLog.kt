package com.team2.studentfitness.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "macro_logs")
data class MacroLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "calories")
    val calories: Int,
    @ColumnInfo(name = "protein")
    val protein: Int,
    @ColumnInfo(name = "carbs")
    val carbs: Int,
    @ColumnInfo(name = "fat")
    val fat: Int,
    @ColumnInfo(name = "fiber")
    val fiber: Int = 0,
    @ColumnInfo(name = "sugar")
    val sugar: Int = 0,
    @ColumnInfo(name = "sodium")
    val sodium: Int = 0,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

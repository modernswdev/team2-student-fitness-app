package com.team2.studentfitness.ui.screens

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "journal_entries")
data class JournalEntry(
    val date: String,
    val content: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Dao
interface JournalDao {
    @Insert
    suspend fun insertEntry(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>
}

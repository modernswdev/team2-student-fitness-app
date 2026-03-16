package com.team2.studentfitness

import android.app.Application
import com.team2.studentfitness.database.Database

class DatabaseCreation : Application() {
    // Creates a singleton instance of the database
    val database: Database by lazy { Database.getDatabase(this) }
}

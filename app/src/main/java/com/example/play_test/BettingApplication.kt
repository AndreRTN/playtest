package com.example.play_test

import android.app.Application
import com.example.play_test.data.dao.AppDatabase

class BettingApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
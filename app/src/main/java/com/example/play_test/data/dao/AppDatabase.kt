package com.example.play_test.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.play_test.data.models.Balance
import com.example.play_test.data.models.BetHistory

@Database(entities = [BetHistory::class, Balance::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun betHistoryDao(): BetHistoyDao
    abstract fun balanceDao(): BalanceDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "bet_history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}

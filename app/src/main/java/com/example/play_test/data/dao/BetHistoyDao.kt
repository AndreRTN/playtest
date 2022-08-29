package com.example.play_test.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.play_test.data.models.BetHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface BetHistoyDao {

    @Query("SELECT * FROM BetHistory")
    fun getAll(): Flow<List<BetHistory>>

    @Insert
    fun add(betHistory: BetHistory)
}

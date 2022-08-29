package com.example.play_test.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.play_test.data.models.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Insert
    fun addBalance(balance: Balance)

    @Update
    fun update(balance: Balance)

    @Query("SELECT * FROM Balance")
    fun getBalance(): Flow<List<Balance>>
}

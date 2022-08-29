package com.example.play_test.ui.myBets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.play_test.data.dao.BetHistoyDao
import com.example.play_test.data.models.BetHistory
import kotlinx.coroutines.flow.Flow

class MyBetsViewModel(val dao: BetHistoyDao) : ViewModel() {

    fun getBets(): Flow<List<BetHistory>> {
        return dao.getAll()
    }
}

class MyBetsViewModelFactory(private val dao: BetHistoyDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyBetsViewModel::class.java)) {
            return MyBetsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

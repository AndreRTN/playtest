package com.example.play_test.ui.currentBets

interface RecyclerCartsBetListener {

    fun onRemove(position: Int)
    fun validatePrice(position: Int, price: String): Boolean
    fun isInitialPriceValid(price: Double): Boolean
}
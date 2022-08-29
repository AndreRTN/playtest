package com.example.play_test.ui.bets

import com.example.play_test.data.pojo.CartItemPojo

interface RecyclerBetsListener {
    fun addToCart(cartItemPojo: CartItemPojo)
}

interface RecyclerOddsListener {
    fun addOdd(oddPojo: OddPojo)
    fun removeOdd(oddPojo: OddPojo)
}
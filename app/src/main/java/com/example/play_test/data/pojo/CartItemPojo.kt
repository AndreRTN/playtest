package com.example.play_test.data.pojo

import com.example.play_test.ui.bets.OddPojo

data class CartItemPojo(
    val id: String,
    var odd: OddPojo,
    var draw: Boolean,
    var winTeam: String,
    var loseTeam: String,
    var value: Double,
    val homeTeam: String,
    val awayTeam: String,
    var isValid: Boolean = false,
    val date: String,
)

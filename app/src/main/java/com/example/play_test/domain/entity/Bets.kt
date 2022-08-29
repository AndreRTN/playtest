package com.example.play_test.domain.entity

import com.example.play_test.ui.bets.OddPojo

data class Bets(
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    val homeTeam: String,
    val awayTeam: String,
    var winTeam: String = "",
    var loseTeam: String = "",
    var win: Boolean = false,
    var draw: Boolean = false,
    var ended: Boolean = false,

    val date: String,
    var timeToEnd: Int,
    val id: String,
    val value: Double,
    val oddPojo: OddPojo,
)

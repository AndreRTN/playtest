package com.example.play_test.data.dto

import com.example.play_test.ui.bets.OddPojo
import com.google.gson.annotations.SerializedName

data class PredictionDataResponse(
    @SerializedName("data")
    val data: List<PredictionResponse>
)

data class PredictionResponse(
    @SerializedName("home_team")
    val homeTeam: String,

    @SerializedName("away_team")
    val awayTeam: String,

    val id: Long,
    val market: String,

    @SerializedName("competition_name")
    val competitionName: String,

    val prediction: String,

    @SerializedName("competition_cluster")
    val competitionCluster: String,

    val status: String,
    val federation: String,

    @SerializedName("is_expired")
    val isExpired: Boolean,

    val season: String,
    val result: String,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("last_update_at")
    val lastUpdateAt: String,

    @SerializedName("home_strength")
    val homeStrength: Double,

    @SerializedName("away_strength")
    val awayStrength: Double,

    val odds: Odds
)

data class Odds(
    @SerializedName("1")
    val the1: Double,

    @SerializedName("2")
    val the2: Double,

    @SerializedName("12")
    val the12: Double,

    @SerializedName("1X")
    val the1X: Double,

    @SerializedName("X2")
    val x2: Double,

    @SerializedName("X")
    val x: Double
)

fun Odds.toPojo(predictionId: String): List<OddPojo> {
    val addsPojo = mutableListOf<OddPojo>()
    val one = OddPojo(predictionId, id = "1", checked = false, odd = the1)
    val two = OddPojo(predictionId, id = "2", checked = false, odd = the2)
    val x = OddPojo(predictionId, id = "X", checked = false, odd = x)
    addsPojo.add(one)
    addsPojo.add(two)
    addsPojo.add(x)
    return addsPojo
}

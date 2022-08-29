package com.example.play_test.ui.bets

import androidx.lifecycle.ViewModel
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.data.pojo.CartItemPojo
import com.example.play_test.data.pojo.OddsPredictionPojo

class BetsViewModel : ViewModel() {
    var selectedPredictions: List<PredictionResponse>? = null
    val selectedOdds: MutableList<OddsPredictionPojo> = mutableListOf()
    fun selectPrediction(predictions: List<PredictionResponse>) {
        selectedPredictions = predictions
    }

    fun addOdd(odd: OddPojo) {
        val hasOdd = selectedOdds.find { it.predictionResponse.id.toString() == odd.predictionId }
        if (hasOdd != null) selectedOdds.remove(hasOdd)

        val findPrediction = selectedPredictions?.find { it.id.toString() == odd.predictionId }
        val oddsPredictionPojo = OddsPredictionPojo(predictionResponse = findPrediction!!, odd)
        selectedOdds.add(oddsPredictionPojo)
    }

    fun clearList() {
        selectedOdds.clear()
    }

    fun mountCartItem(predictionResponse: PredictionResponse, odd: OddPojo): CartItemPojo {
        val resultOdd = checkOdd(odd)
        var winTeam = ""
        var loseTeam = ""
        var draw = false
        when (resultOdd) {
            1 -> {
                winTeam = predictionResponse.homeTeam
                loseTeam = predictionResponse.awayTeam
            }
            2 -> {
                winTeam = predictionResponse.awayTeam
                loseTeam = predictionResponse.homeTeam
            }
            else -> {
                draw = true
            }
        }
        return CartItemPojo(
            id = predictionResponse.id.toString(),
            draw = draw,
            odd = odd,
            loseTeam = loseTeam,

            winTeam = winTeam,
            homeTeam = predictionResponse.homeTeam,
            awayTeam = predictionResponse.awayTeam,
            value = 0.0,
            date = predictionResponse.startDate,
        )
    }

    private fun checkOdd(oddPojo: OddPojo): Int {
        return when (oddPojo.id) {
            "1" -> 1
            "2" -> 2
            "X" -> 0
            else -> 0
        }
    }

    fun removeOdd(odd: OddPojo) {

        val hasOdd = selectedOdds.find { it.predictionResponse.prediction == odd.predictionId }
        hasOdd?.let { selectedOdds.remove(it) }
    }

    fun removeOddFromPosition(position: Int) {
        selectedOdds.removeAt(position)
    }
}

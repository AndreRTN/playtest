package com.example.play_test.data.pojo

import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.ui.bets.OddPojo

data class OddsPredictionPojo(
    val predictionResponse: PredictionResponse,
    val odd: OddPojo
)

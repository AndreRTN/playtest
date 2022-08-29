package com.example.play_test.ui.home

import com.example.play_test.data.dto.PredictionResponse

interface RecyclerPredictionsListener {
    fun onClickPrediction(predictions: List<PredictionResponse>)
}
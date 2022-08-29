package com.example.play_test.domain.usecases

import com.example.play_test.data.api.ResultApi
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.data.enums.Federations

interface GetPredictionsUseCase {
    suspend fun getPredictions(
        federation: Federations = Federations.UEFA,
        isoDate: String
    ): ResultApi<List<PredictionResponse>>
}

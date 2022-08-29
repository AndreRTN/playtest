package com.example.play_test.data.repository

import com.example.play_test.data.api.ResultApi
import com.example.play_test.data.api.calls.PredictionsHTTP
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.data.enums.Federations
import com.example.play_test.domain.usecases.GetPredictionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.awaitResponse
import java.lang.Exception

class PredictionsRepository(private val service: PredictionsHTTP) : GetPredictionsUseCase {
    override suspend fun getPredictions(
        federation: Federations,
        isoDate: String
    ): ResultApi<List<PredictionResponse>> {
        val result = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = service.getPredictions(isoDate, federation.name).awaitResponse()
                if (response.isSuccessful) {
                    ResultApi.Success(response.body()?.data)
                } else {
                    ResultApi.Error(response.message())
                }
            } catch (e: Exception) {
                ResultApi.Error(e.message.toString())
            }
        }
        return result.await()
    }
}

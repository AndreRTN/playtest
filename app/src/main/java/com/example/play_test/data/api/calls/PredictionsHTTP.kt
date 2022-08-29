package com.example.play_test.data.api.calls

import com.example.play_test.data.dto.PredictionDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PredictionsHTTP {

    @GET("predictions?")
    fun getPredictions(
        @Query("iso_date") isoDate: String,
        @Query("federation") federationId: String
    ): Call<PredictionDataResponse>
}

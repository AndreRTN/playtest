package com.example.play_test.data.api


import com.example.play_test.BuildConfig
import com.example.play_test.data.api.calls.PredictionsHTTP
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIClient {


    const val BASE_URL = BuildConfig.API_URL

    private var retrofit: Retrofit? = null
    private var okHttpClient = OkHttpClient.Builder().apply {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        addInterceptor(

            Interceptor { chain ->
                val builder = chain.request().newBuilder()

                builder.header("X-RapidAPI-Host", BuildConfig.API_HOST)
                builder.header("X-RapidAPI-Key", BuildConfig.API_KEY)
                return@Interceptor chain.proceed(builder.build())
            }
        ).addInterceptor(interceptor)
    }.build()

    private fun getClient(): Retrofit {

        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit!!
    }

    fun predictionsService(): PredictionsHTTP {
        return getClient().create(PredictionsHTTP::class.java)
    }



}
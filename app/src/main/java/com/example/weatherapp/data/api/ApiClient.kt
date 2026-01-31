package com.example.weatherapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val geocodingApi: GeocodingApi =
        retrofit("https://geocoding-api.open-meteo.com/")
            .create(GeocodingApi::class.java)

    val weatherApi: WeatherApi =
        retrofit("https://api.open-meteo.com/")
            .create(WeatherApi::class.java)
}

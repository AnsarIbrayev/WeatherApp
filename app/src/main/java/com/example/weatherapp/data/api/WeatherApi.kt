package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        // current
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m",
        // daily forecast (3+ days)
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
        @Query("forecast_days") forecastDays: Int = 3,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

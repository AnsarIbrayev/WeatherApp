package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.ApiClient
import com.example.weatherapp.data.model.WeatherResponse

class WeatherRepository {

    suspend fun getWeatherByCity(city: String): Pair<String, WeatherResponse> {
        val geo = ApiClient.geocodingApi.searchCity(name = city, count = 1)
        val first = geo.results?.firstOrNull()
            ?: throw IllegalArgumentException("City not found")

        val weather = ApiClient.weatherApi.getWeather(
            latitude = first.latitude,
            longitude = first.longitude,
            forecastDays = 3
        )

        val cityTitle = buildString {
            append(first.name)
            if (!first.country.isNullOrBlank()) append(", ").append(first.country)
        }

        return cityTitle to weather
    }
}

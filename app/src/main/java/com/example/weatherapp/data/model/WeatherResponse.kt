package com.example.weatherapp.data.model

data class WeatherResponse(
    val current: CurrentWeather?,
    val daily: DailyWeather?
)

data class CurrentWeather(
    val temperature_2m: Double?,
    val relative_humidity_2m: Int?,
    val wind_speed_10m: Double?
)

data class DailyWeather(
    val time: List<String>?,
    val temperature_2m_max: List<Double>?,
    val temperature_2m_min: List<Double>?
)

package com.example.weatherapp.viewmodel

import com.example.weatherapp.data.model.WeatherResponse

sealed class WeatherUiState {
    data object Idle : WeatherUiState()
    data object Loading : WeatherUiState()
    data class Success(
        val cityTitle: String,
        val data: WeatherResponse,
        val isOffline: Boolean = false
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

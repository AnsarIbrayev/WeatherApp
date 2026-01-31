package com.example.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.NetworkUtils
import com.example.weatherapp.data.local.WeatherCache
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val appContext: Context,
    private val repo: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val cache = WeatherCache(appContext)

    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    fun loadCachedIfExists() {
        val cached = cache.load() ?: return
        _state.value = WeatherUiState.Success(
            cityTitle = cached.first,
            data = cached.second,
            isOffline = true
        )
    }

    fun search(city: String) {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) {
            _state.value = WeatherUiState.Error("Enter a city name")
            return
        }

        // если нет интернета — показываем кеш (если есть)
        if (!NetworkUtils.isOnline(appContext)) {
            val cached = cache.load()
            _state.value = if (cached != null) {
                WeatherUiState.Success(cached.first, cached.second, isOffline = true)
            } else {
                WeatherUiState.Error("No internet and no cached data")
            }
            return
        }

        _state.value = WeatherUiState.Loading

        viewModelScope.launch {
            try {
                val (cityTitle, weather) = repo.getWeatherByCity(trimmed)

                // сохраняем в кеш
                cache.save(cityTitle, weather)

                _state.value = WeatherUiState.Success(
                    cityTitle = cityTitle,
                    data = weather,
                    isOffline = false
                )
            } catch (e: IllegalArgumentException) {
                _state.value = WeatherUiState.Error("City not found")
            } catch (e: Exception) {
                // если сеть упала — попробуем кеш
                val cached = cache.load()
                _state.value = if (cached != null) {
                    WeatherUiState.Success(cached.first, cached.second, isOffline = true)
                } else {
                    WeatherUiState.Error("Network/API error. Try again.")
                }
            }
        }
    }
}

package com.example.weatherapp.data.local

import android.content.Context
import com.example.weatherapp.data.model.WeatherResponse
import com.google.gson.Gson

class WeatherCache(context: Context) {
    private val prefs = context.getSharedPreferences("weather_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun save(cityTitle: String, data: WeatherResponse) {
        prefs.edit()
            .putString("cityTitle", cityTitle)
            .putString("weatherJson", gson.toJson(data))
            .apply()
    }

    fun load(): Pair<String, WeatherResponse>? {
        val title = prefs.getString("cityTitle", null) ?: return null
        val json = prefs.getString("weatherJson", null) ?: return null
        return title to gson.fromJson(json, WeatherResponse::class.java)
    }
}

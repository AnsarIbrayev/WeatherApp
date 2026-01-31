package com.example.weatherapp.data.model

data class GeocodingResponse(
    val results: List<GeoCity>?
)

data class GeoCity(
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)

package com.example.weatherapp.favorites

data class FavoriteItem(
    var id: String = "",
    var title: String = "",
    var note: String = "",
    var createdAt: Long = 0L,
    var createdBy: String = ""
)

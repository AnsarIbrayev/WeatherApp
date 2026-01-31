package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.model.DailyWeather
import com.example.weatherapp.viewmodel.WeatherUiState
import com.example.weatherapp.viewmodel.WeatherViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Switch

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val state by viewModel.state.collectAsState()
    var cityInput by remember { mutableStateOf("") }
    var useF by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Search row
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("City") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(Modifier.width(12.dp))
            Button(onClick = { viewModel.search(cityInput) }) {
                Text("Search")
            }
        }
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (useF) "Unit: °F" else "Unit: °C")
            Switch(
                checked = useF,
                onCheckedChange = { useF = it }
            )
        }

        Spacer(Modifier.height(12.dp))


        Spacer(Modifier.height(16.dp))

        when (val s = state) {
            is WeatherUiState.Idle -> {
                Text("Enter a city and press Search.")
            }

            is WeatherUiState.Loading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Loading...")
                }
            }

            is WeatherUiState.Error -> {
                Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WeatherUiState.Success -> {
                WeatherContent(
                    cityTitle = s.cityTitle,
                    isOffline = s.isOffline,
                    currentTemp = s.data.current?.temperature_2m,
                    humidity = s.data.current?.relative_humidity_2m,
                    wind = s.data.current?.wind_speed_10m,
                    daily = s.data.daily,
                    useF = useF
                )

            }
        }
    }
}

@Composable
private fun WeatherContent(
    cityTitle: String,
    isOffline: Boolean,
    currentTemp: Double?,
    humidity: Int?,
    wind: Double?,
    daily: DailyWeather?,
    useF: Boolean
){
    if (isOffline) {
        Text("OFFLINE", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
    }

    Text(text = cityTitle, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))

    Text("Temperature: ${formatTemp(currentTemp, useF)}")
    Text("Humidity: ${humidity?.toString() ?: "—"} %")
    Text("Wind: ${wind?.toString() ?: "—"} m/s")

    Spacer(Modifier.height(16.dp))
    Text("Forecast (3 days)", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    val days = daily?.time.orEmpty()
    val maxT = daily?.temperature_2m_max.orEmpty()
    val minT = daily?.temperature_2m_min.orEmpty()

    val itemsList = days.indices.map { i ->
        Triple(
            days.getOrNull(i) ?: "—",
            maxT.getOrNull(i),
            minT.getOrNull(i)
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(itemsList) { (day, max, min) ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(day)
                    Text("max ${formatTemp(max, useF)} / min ${formatTemp(min, useF)}")
                }
            }
        }
    }
}

private fun formatTemp(v: Double?, useF: Boolean): String {
    if (v == null) return "—"
    val value = if (useF) (v * 9.0 / 5.0) + 32.0 else v
    val unit = if (useF) "°F" else "°C"
    return "${"%.1f".format(value)} $unit"
}
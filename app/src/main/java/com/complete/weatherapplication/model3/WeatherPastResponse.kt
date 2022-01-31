package com.complete.weatherapplication.model3

data class WeatherPastResponse(
    val current: Current?,
    val hourly: List<Hourly>?,
    val lat: Double?,
    val lon: Double?,
    val timezone: String?,
    val timezone_offset: Int?
)
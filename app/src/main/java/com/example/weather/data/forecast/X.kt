package com.example.weather.data.forecast

data class X(
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    val main: Main,
    val snow: Snow,
    val sys: Sys,
    val weather: List<Weather>,
    val wind: Wind
)
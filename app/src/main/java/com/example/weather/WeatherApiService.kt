package com.example.weather
import com.example.weather.data.Weather
import com.example.weather.data.WeatherForecastResponse
import com.google.gson.Gson
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


const val city_id = 536203
const val apiKey = "eec81c834f077f6cf736dafe4e6d8efb"
const val baseUrl = "https://api.openweathermap.org"

interface WeatherApiService {
    @GET("/data/2.5/forecast")
    fun getWeather(@Query("appid") key : String = apiKey,
                   @Query("id") id: Int = city_id
    ): Observable<WeatherForecastResponse>

    companion object {
        fun create(): WeatherApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(WeatherApiService::class.java)
        }
    }
}
package com.example.weather
import com.example.weather.data.current.WeatherCurrentResponse
import com.example.weather.data.forecast.WeatherForecastResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


const val CITY_ID = 536203
const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val API_KEY = BuildConfig.apiKey

interface WeatherApiService {
    @GET("forecast")
    fun getForecastWeather(@Query("appid") key : String = API_KEY,
                   @Query("id") id: Int = CITY_ID
    ): Observable<WeatherForecastResponse>

    @GET("weather")
    fun getCurrentWeather(@Query("appid") key : String = API_KEY,
                          @Query("id") id: Int = CITY_ID
    ): Observable<WeatherCurrentResponse>


    companion object {
        fun create(): WeatherApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(WeatherApiService::class.java)
        }
    }
}
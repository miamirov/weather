package com.example.weather

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.weather.data.current.WeatherCurrentResponse
import com.example.weather.data.forecast.WeatherForecastResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val weatherApiService = WeatherApiService.create()
        weatherApiService
            .getForecastWeather()
            .observeOn(AndroidSchedulers.mainThread()).subscribeOn(
                Schedulers.io()
            )
            .subscribe({ result -> pushForecastData(result) }, { getValuesFromCache() })
        weatherApiService.getCurrentWeather()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(
                Schedulers.io()
            )
            .subscribe({ result -> pushCurrentData(result) }, {
                getValuesFromCache()
            })
    }

    private fun getValuesFromCache() {
        val textFields = listOf<TextView>(day0, day1, day2, day3, day4, windFlow, humidity)
        val sh = getPreferences(Context.MODE_PRIVATE)
        textFields.forEachIndexed { i, it ->
            if (it.text.isEmpty()) {
                it.text = sh.getString("textField$i", "No data")
            }
        }
    }

    private fun pushForecastData(r: WeatherForecastResponse) {
        val date = Calendar.getInstance()
        val x = listOf<TextView>(day1, day2, day3, day4)
        val twelveHours = r.list.drop(3).filter { it.dt % 86400 == 43200 }.take(4)
        x.forEachIndexed { i, it ->
            date.add(Calendar.DATE, 1)
            val weekDay = date.get(Calendar.DAY_OF_WEEK)
            it.text = when (weekDay) {
                1 -> "Sun"
                2 -> "Mon"
                3 -> "Tue"
                4 -> "Thu"
                5 -> "Wen"
                6 -> "Fri"
                7 -> "Sat"
                else -> "wtf"
            } + ": ${twelveHours[i].main.temp} K"

        }
        cacheData()

    }

    private fun pushCurrentData(r: WeatherCurrentResponse) {
        humidity.text = r.main.humidity.toString()
        day0.text = r.main.temp.toString() + " K"
        windFlow.text = r.wind.speed.toString()
        cacheData()


    }
    private fun cacheData() {
        val textFields = listOf<TextView>(day0, day1, day2, day3, day4, humidity, windFlow)
        with (getPreferences(Context.MODE_PRIVATE).edit()) {
            textFields.forEachIndexed { i, it ->
                putString("textField$i", it.text.toString())
            }
            commit()
        }
    }

}

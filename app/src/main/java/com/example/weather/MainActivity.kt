package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.MainThread
import com.example.weather.data.WeatherForecastResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val weatherApiService = WeatherApiService.create()
        weatherApiService
            .getWeather()
            .observeOn(AndroidSchedulers.mainThread()).subscribeOn(
                Schedulers.io()
            ).subscribe({ result -> pushData(result) }, { error -> })
    }

    fun pushData(r: WeatherForecastResponse) {
        humidity.text = r.list[0].main.humidity.toString()
        windFlow.text = r.list[0].wind.speed.toString()
    }
}

package com.example.weather

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.data.current.WeatherCurrentResponse
import com.example.weather.data.forecast.WeatherForecastResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var disposable: CompositeDisposable = CompositeDisposable()
    var ids = arrayOfNulls<String>(5)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val weatherApiService = WeatherApiService.create()
        disposable.add(
            weatherApiService
                .getForecastWeather()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result -> pushForecastData(result) }, { getValuesFromCache() })
        )
        disposable.add(
            weatherApiService.getCurrentWeather()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result -> pushCurrentData(result) }, {
                    getValuesFromCache()
                })
        )

    }

    fun getImage(s: String?): Int {
        return when (s) {
            "01d" -> R.drawable.sun
            "02d" -> R.drawable.sun_cloudy
            "03d" -> R.drawable.cloud
            "04d" -> R.drawable.cloud
            "09d" -> R.drawable.rain
            "10d" -> R.drawable.sun_rain
            "11d" -> R.drawable.rain
            else -> R.drawable.cloud
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun getValuesFromCache() {
        val textFields = listOf<TextView>(day0, day1, day2, day3, day4, humidity, windFlow)
        val images = listOf<ImageView>(icon0, icon1, icon2, icon3, icon4)
        val sh = getPreferences(Context.MODE_PRIVATE)
        textFields.forEachIndexed { i, it ->
            if (it.text.isEmpty()) {
                it.text = sh.getString("textField$i", "No data")
            }
        }
        images.forEachIndexed { i, it ->
            ids[i] = sh.getString("imageField$i", "01d")
            it.setImageResource(getImage(ids[i]))
        }
    }



private fun pushForecastData(r: WeatherForecastResponse) {
    val date = Calendar.getInstance()
    val days = listOf<TextView>(day1, day2, day3, day4)
    val images = listOf<ImageView>(icon1, icon2, icon3, icon4)
    val twelveHours = r.list.drop(3).filter { it.dt % 86400 == 43200 }.take(4)
    days.forEachIndexed { i, it ->
        date.add(Calendar.DATE, 1)
        val stringDay = resources.getString(
            when (date.get(Calendar.DAY_OF_WEEK)) {
                1 -> R.string.sunday
                2 -> R.string.monday
                3 -> R.string.tuesday
                4 -> R.string.wednesday
                5 -> R.string.thursday
                6 -> R.string.friday
                else -> R.string.saturday
            }
        )
        it.text =
            resources.getString(
                R.string.dayWeather,
                stringDay,
                (twelveHours[i].main.temp - 273).toInt()
            )

    }
    images.forEachIndexed { i, it ->
        ids[i + 1] = twelveHours[i].weather[0].icon
        it.setImageResource(getImage(ids[i + 1]))

    }
    cacheData()

}

private fun pushCurrentData(r: WeatherCurrentResponse) {
    humidity.text = r.main.humidity.toString()
    day0.text = resources.getString(R.string.weather, (r.main.temp - 273).toInt())
    ids[0] = r.weather[0].icon
    icon0.setImageResource(getImage(ids[0]))
    windFlow.text = r.wind.speed.toString()
    cacheData()
}

private fun cacheData() {
    val images = listOf<ImageView>(icon0, icon1, icon2, icon3, icon4)

    val textFields = listOf<TextView>(day0, day1, day2, day3, day4, humidity, windFlow)
    with(getPreferences(Context.MODE_PRIVATE).edit()) {
        textFields.forEachIndexed { i, it ->
            putString("textField$i", it.text.toString())
        }
        images.forEachIndexed { i, _ ->
            putString("imageField$i", ids[i])
        }
        commit()
    }
}

}

package com.astatema.weatherapp.apicalls;

import com.astatema.weatherapp.datamodels.CityWeather;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface WeatherService {
    @GET("free/v2/weather.ashx")
    Observable<CityWeather> getWeather(@QueryMap Map<String, String> params);
}

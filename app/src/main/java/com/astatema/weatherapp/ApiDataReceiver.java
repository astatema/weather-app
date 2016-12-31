package com.astatema.weatherapp;

import com.astatema.weatherapp.datamodels.CityWeather;

public interface ApiDataReceiver {
    void onDataReceiveStarted();
    void onDataItemReceived(CityWeather weatherData);
    void onDataReceiveFinished();
}

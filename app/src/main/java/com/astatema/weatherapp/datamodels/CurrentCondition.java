package com.astatema.weatherapp.datamodels;

import java.util.List;

public class CurrentCondition {
    private String observation_time;
    private int temp_C;
    private int FeelsLikeC;
    private int windspeedKmph;
    private List<Value> weatherDesc;
    private List<Value> weatherIconUrl;
    private float precipMM;
    private float humidity;
    private int pressure;

    public String getObservation_time() {
        return observation_time;
    }

    public int getTemp_C() {
        return temp_C;
    }

    public int getFeelsLikeC() {
        return FeelsLikeC;
    }

    public int getWindspeedKmph() {
        return windspeedKmph;
    }

    public String getWeatherDesc() {
        return weatherDesc.get(0).getValue();
    }

    public String getWeatherIconUrl() {
        return weatherIconUrl.get(0).getValue();
    }

    public float getPrecipMM() {
        return precipMM;
    }

    public float getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }
}
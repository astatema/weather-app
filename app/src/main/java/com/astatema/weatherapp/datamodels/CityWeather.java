package com.astatema.weatherapp.datamodels;

public class CityWeather {
    private Data data;

    public String getCityName() {
        String[] queryParts = data.getRequest().getQuery().split(",");
        return queryParts[0];
    }

    public int getTemperature() {
        return data.getCurrent_condition().getTemp_C();
    }

    public int getFeelsTemp() {
        return data.getCurrent_condition().getFeelsLikeC();
    }

    public String getWeatherIconUrl() {
        return data.getCurrent_condition().getWeatherIconUrl();
    }

    public String getWeatherCondition() {
        return data.getCurrent_condition().getWeatherDesc();
    }

    public float getPrecipitation() {
        return data.getCurrent_condition().getPrecipMM();
    }

    public float getHumidity() {
        return data.getCurrent_condition().getHumidity();
    }

    public int getPressure() {
        return data.getCurrent_condition().getPressure();
    }

    public int getWindSpeed() {
        return data.getCurrent_condition().getWindspeedKmph();
    }

    public String getTimestamp() {
        return data.getCurrent_condition().getObservation_time();
    }
}

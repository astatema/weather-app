package com.astatema.weatherapp.apicalls;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequester {
    private static WeatherService instance = null;

    private static final String BASE_URL = "http://api.worldweatheronline.com/";

    private RetrofitRequester() {}

    public static WeatherService getInstance() {
        if(instance == null) {
            init();
        }

        return instance;
    }

    private static void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        instance = retrofit.create(WeatherService.class);
    }
}

package com.astatema.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.astatema.weatherapp.adapters.WeatherAdapter;
import com.astatema.weatherapp.datamodels.CityWeather;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        ApiDataReceiver{
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView citiesRecyclerView;
    private WeatherAdapter citiesAdapter;

    private ConnectivityManager manager;

    public static final String DATA_KEY = "weather_json";
    public static final String TIME_KEY = "timestamp";

    private ArrayList<CityWeather> weatherList = new ArrayList<>();

    private boolean isUpdateInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        citiesRecyclerView = (RecyclerView) findViewById(R.id.cities_recycler_view);

        //setting up the list
        citiesRecyclerView.setHasFixedSize(true);
        citiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        citiesAdapter = new WeatherAdapter(weatherList, citiesRecyclerView, getResources());
        citiesRecyclerView.setAdapter(citiesAdapter);

        //pull-to-refresh action will be listened by this activity
        swipeRefreshLayout.setOnRefreshListener(this);

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveData(); //get cached data first
        citiesAdapter.updateListView(weatherList);

        WeatherUpdater.getInstance(this, manager).start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        WeatherUpdater.getInstance(this, manager).stop();
    }

    @Override
    public void onDataReceiveStarted() {
        isUpdateInProgress = true;
        weatherList.clear();
    }

    @Override
    public void onDataItemReceived(CityWeather weatherData) {
        weatherList.add(weatherData);
    }

    @Override
    public void onDataReceiveFinished() {
        isUpdateInProgress = false;
        citiesAdapter.updateListView(weatherList);
        cacheData();

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (!isUpdateInProgress) {
            WeatherUpdater.getInstance(this, manager).refreshWeatherInfo();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void cacheData() {
        Gson gson = new Gson();
        String json = gson.toJson(weatherList);
        getDefaultSharedPreferences(this)
                .edit()
                .putLong(TIME_KEY, Calendar.getInstance().getTimeInMillis())
                .putString(DATA_KEY, json)
                .apply();
    }

    private void retrieveData() {
        Gson gson = new Gson();
        String json = getDefaultSharedPreferences(this).getString(DATA_KEY, "");
        if (!"".equals(json)) {
            CityWeather[] dataArray = gson.fromJson(json, CityWeather[].class);
            weatherList = new ArrayList<>(Arrays.asList(dataArray));
        }
    }
}
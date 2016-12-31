package com.astatema.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.astatema.weatherapp.apicalls.RetrofitRequester;
import com.astatema.weatherapp.apicalls.WeatherService;
import com.astatema.weatherapp.datamodels.CityWeather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.astatema.weatherapp.MainActivity.TIME_KEY;

/**
 * This class handles communication between the server and the app
 */
public class WeatherUpdater {
    private static WeatherUpdater instance = null;

    private Observable<CityWeather> mergedObservable;
    private ApiDataReceiver listener; //contains api callback functions
    private ConnectivityManager connectivityManager;

    private static final String API_KEY = "5ed0d8b647f24d81a9f105723161603";
    private static final long TEN_MINUTES_IN_MILLIS = 600000;
    private static final String[] CITIES = new String[] {
            "Sydney,Australia",
            "Melbourne,Australia",
            "Brisbane,Australia",
            "Adelaide,Australia",
            "Perth,Australia",
            "Hobart,Australia",
            "Darwin,Australia"};

    private Handler handler;
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            refreshWeatherInfo();

            handler.postDelayed(refresh, TEN_MINUTES_IN_MILLIS);
        }
    };

    private WeatherUpdater() {}

    public static WeatherUpdater getInstance(ApiDataReceiver lis, ConnectivityManager manager) {
        if(instance == null) {
            instance = new WeatherUpdater(lis, manager);
        }

        return instance;
    }

    private WeatherUpdater(ApiDataReceiver lis, ConnectivityManager manager) {
        handler = new Handler();
        listener = lis;
        connectivityManager = manager;

        //common parameters for all requests
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("key", API_KEY);
        tempMap.put("num_of_days", "1");
        tempMap.put("format", "json");

        ArrayList<Observable<CityWeather>> list = new ArrayList<>();
        WeatherService service = RetrofitRequester.getInstance();
        //generating observables for each city
        for(String city : CITIES) {
            Map<String, String> map = new HashMap<>(tempMap);
            map.put("q", city);
            list.add(service.getWeather(map));
        }

        //merging observables
        mergedObservable = Observable.mergeDelayError(list)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void start(Context context) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cachedTime = getDefaultSharedPreferences(context).getLong(TIME_KEY, 0);

        //we do not start weather updates unless at least 10 min has passed since the last one
        if ((currentTime - cachedTime) >= TEN_MINUTES_IN_MILLIS) {
            handler.post(refresh);
        } else {
            handler.postDelayed(refresh, TEN_MINUTES_IN_MILLIS - (currentTime - cachedTime));
        }
    }

    public void stop() {
        handler.removeCallbacks(refresh);
    }

    public void refreshWeatherInfo() {
        //check if the Internet connection is ok
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected)
            return;

        listener.onDataReceiveStarted();
        mergedObservable.subscribe(new Subscriber<CityWeather>() {
            @Override
            public void onCompleted() {
                listener.onDataReceiveFinished();
            }

            @Override
            public void onError(Throwable e) {
                //There is no error handling. We just show whatever we received.
                listener.onDataReceiveFinished();
            }

            @Override
            public void onNext(CityWeather weather) {
                listener.onDataItemReceived(weather);
            }
        });
    }
}

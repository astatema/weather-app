package com.astatema.weatherapp.adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astatema.weatherapp.R;
import com.astatema.weatherapp.datamodels.CityWeather;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> implements View.OnClickListener {
    private HashMap<String, Boolean> favouriteCities = new HashMap<>();

    private RecyclerView mRecyclerView;
    private Resources res;

    private ArrayList<CityWeather> weatherDataset;

    public WeatherAdapter(ArrayList<CityWeather> dataset, RecyclerView view, Resources r) {
        weatherDataset = dataset;
        mRecyclerView = view;
        res = r;

        //all cities are not favourite by default
        favouriteCities.put("Sydney", false);
        favouriteCities.put("Melbourne", false);
        favouriteCities.put("Brisbane", false);
        favouriteCities.put("Adelaide", false);
        favouriteCities.put("Perth", false);
        favouriteCities.put("Hobart", false);
        favouriteCities.put("Darwin", false);
    }

    public void updateListView(ArrayList<CityWeather> dataset) {
        weatherDataset = dataset;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_listview, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (favouriteCities.get(weatherDataset.get(position).getCityName())) {
            holder.baseView.setBackgroundColor(Color.parseColor("#CCCCCC"));
            holder.extraInfoView.setVisibility(View.VISIBLE);
        } else {
            holder.baseView.setBackgroundColor(Color.WHITE);
            holder.extraInfoView.setVisibility(View.GONE);
        }

        new DownloadImageTask(holder.icon).execute(weatherDataset.get(position).getWeatherIconUrl());

        holder.nameView.setText(weatherDataset.get(position).getCityName());
        holder.temperatureView.setText(formatText(R.string.temp_text, weatherDataset.get(position).getTemperature() + ""));
        holder.feelsTempView.setText(formatText(R.string.feels_temp_text, weatherDataset.get(position).getFeelsTemp() + ""));
        holder.weatherConditionView.setText(weatherDataset.get(position).getWeatherCondition());
        holder.precipitationView.setText(formatText(R.string.precipitation_text, ((int) weatherDataset.get(position).getPrecipitation()) + ""));
        holder.humidityView.setText(formatText(R.string.humidity_text, ((int) (weatherDataset.get(position).getHumidity() * 100f)) + ""));
        holder.pressureView.setText(formatText(R.string.pressure_text, weatherDataset.get(position).getPressure() + ""));
        holder.windSpeedView.setText(formatText(R.string.wind_speed_text, weatherDataset.get(position).getWindSpeed() + ""));
        holder.timestampView.setText(formatText(R.string.time_text, weatherDataset.get(position).getTimestamp()));
    }

    private String formatText(int textId, String text) {
        return String.format(Locale.US, res.getString(textId), text);
    }

    @Override
    public int getItemCount() {
        return weatherDataset.size();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);
        String name = weatherDataset.get(itemPosition).getCityName();
        favouriteCities.put(name, !favouriteCities.get(name));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View baseView;
        public final ImageView icon;
        public final LinearLayout extraInfoView;
        public final TextView nameView;
        public final TextView temperatureView;
        public final TextView feelsTempView;
        public final TextView weatherConditionView;
        public final TextView precipitationView;
        public final TextView humidityView;
        public final TextView pressureView;
        public final TextView windSpeedView;
        public final TextView timestampView;

        public ViewHolder(View view) {
            super(view);
            baseView = view;
            icon = (ImageView) view.findViewById(R.id.city_icon);
            extraInfoView = (LinearLayout) view.findViewById(R.id.favourite_row);
            nameView = (TextView) view.findViewById(R.id.city_name);
            temperatureView = (TextView) view.findViewById(R.id.city_temperature);
            feelsTempView = (TextView) view.findViewById(R.id.city_feels_temp);
            weatherConditionView = (TextView) view.findViewById(R.id.city_weather_condition);
            precipitationView = (TextView) view.findViewById(R.id.city_precipitation);
            humidityView = (TextView) view.findViewById(R.id.city_humidity);
            pressureView = (TextView) view.findViewById(R.id.city_pressure);
            windSpeedView = (TextView) view.findViewById(R.id.city_wind_speed);
            timestampView = (TextView) view.findViewById(R.id.timestamp_view);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

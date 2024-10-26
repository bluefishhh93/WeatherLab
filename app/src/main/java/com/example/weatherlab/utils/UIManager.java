package com.example.weatherlab.utils;

import android.app.Activity;
import android.widget.*;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.weatherlab.R;
import com.example.weatherlab.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class UIManager {
    private final Activity activity;
    private final ActivityMainBinding binding;

    public UIManager(Activity activity, ActivityMainBinding binding) {
        this.activity = activity;
        this.binding = binding;
        setDefaultBackground();
    }

    public void setDefaultBackground() {
        Glide.with(activity)
                .load(R.drawable.spalsh_screen)
                .into(binding.ivBackground);
    }

    public void updateBackground(String weatherMain) {
        int backgroundResId;
        switch (weatherMain.toLowerCase()) {
            case "clear":
                backgroundResId = R.drawable.clear_background;
                break;
            case "clouds":
                backgroundResId = R.drawable.colud_background;
                break;
            case "rain":
            case "drizzle":
                backgroundResId = R.drawable.rain_background;
                break;
            case "thunderstorm":
                backgroundResId = R.drawable.thunder_background;
                break;
            case "snow":
                backgroundResId = R.drawable.snow_background;
                break;
            default:
                backgroundResId = R.drawable.spalsh_screen;
                break;
        }
        Glide.with(activity).load(backgroundResId).into(binding.ivBackground);
    }

    public void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void updateStatusBarColor(String weatherMain) {
        int color = getColorForWeather(weatherMain);
        activity.getWindow().setStatusBarColor(color);
    }

    private  int getColorForWeather(String weatherMain){
        int colorResId;
        switch (weatherMain.toLowerCase()) {
            case "clear":
                colorResId = R.color.clear_sky;
                break;
            case "clouds":
                colorResId = R.color.cloudy;
                break;
            case "rain":
            case "drizzle":
                colorResId = R.color.rainy;
                break;
            case "thunderstorm":
                colorResId = R.color.thunderstorm;
                break;
            case "snow":
                colorResId = R.color.snowy;
                break;
            default:
                colorResId = R.color.default_weather;
                break;
        }
        return ContextCompat.getColor(activity, colorResId);
    }



    public void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
package com.example.weatherlab.viewmodel;


import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherlab.model.WeatherResponse;
import com.example.weatherlab.repository.WeatherRepository;

public class WeatherViewModel extends ViewModel {
    private final WeatherRepository repository;
    private final MutableLiveData<WeatherResponse> weatherData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> currentWeatherSound = new MutableLiveData<>();

    public WeatherViewModel(WeatherRepository repository) {
        this.repository = repository;
    }

    public LiveData<WeatherResponse> getWeatherData() {
        return weatherData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getCurrentWeatherSound() {
        return currentWeatherSound;
    }

    public void getWeatherByCity(String city) {
        loading.setValue(true);
        repository.getWeatherByCity(city, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse response) {
                weatherData.setValue(response);
                updateWeatherSound(response.getWeather()[0].getMain());
                loading.setValue(false);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                loading.setValue(false);
            }
        });
    }

    public void getWeatherByLocation(Location location) {
        loading.setValue(true);
        repository.getWeatherByLocation(location, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse response) {
                weatherData.setValue(response);
                updateWeatherSound(response.getWeather()[0].getMain());
                loading.setValue(false);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                loading.setValue(false);
            }
        });
    }

    private void updateWeatherSound(String weatherCondition) {
        currentWeatherSound.setValue(weatherCondition.toLowerCase());
    }
}
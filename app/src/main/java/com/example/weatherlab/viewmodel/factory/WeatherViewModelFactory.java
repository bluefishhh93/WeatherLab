package com.example.weatherlab.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.weatherlab.repository.WeatherRepository;
import com.example.weatherlab.viewmodel.WeatherViewModel;

public class WeatherViewModelFactory implements ViewModelProvider.Factory {
    private final WeatherRepository repository;

    public WeatherViewModelFactory(WeatherRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WeatherViewModel.class)) {
            return (T) new WeatherViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

package com.example.weatherlab.repository;
import android.location.Location;
import com.example.weatherlab.api.ApiConfig;
import com.example.weatherlab.model.WeatherResponse;
import com.example.weatherlab.model.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private final WeatherService weatherService;

    public interface WeatherCallback {
        void onSuccess(WeatherResponse response);
        void onError(String message);
    }

    public WeatherRepository(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public void getWeatherByCity(String city, final WeatherCallback callback) {
        weatherService.getWeather(city, "metric", ApiConfig.API_KEY)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        callback.onError("Error: " + t.getMessage());
                    }
                });
    }

    public void getWeatherByLocation(Location location, final WeatherCallback callback) {
        weatherService.getWeatherByCoordinates(
                location.getLatitude(),
                location.getLongitude(),
                "metric",
                ApiConfig.API_KEY
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                callback.onError("Error: " + t.getMessage());
            }
        });
    }
}
package com.example.weatherlab.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String cityName,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    @GET("weather")
    Call<WeatherResponse> getWeatherByCoordinates(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}
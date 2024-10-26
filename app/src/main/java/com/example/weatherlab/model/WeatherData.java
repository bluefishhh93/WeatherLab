package com.example.weatherlab.model;

public class WeatherData {
    private String cityName;
    private double temperature;
    private String description;
    private String iconUrl;
    private String weatherMain;
    private double humidity;
    private double pressure;
    private double windSpeed;

    // Constructor
    public WeatherData(String cityName, double temperature, String description,
                       String iconUrl, String weatherMain, double humidity,
                       double pressure, double windSpeed) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.description = description;
        this.iconUrl = iconUrl;
        this.weatherMain = weatherMain;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
    }

    // Getters
    public String getCityName() { return cityName; }
    public double getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public String getIconUrl() { return iconUrl; }
    public String getWeatherMain() { return weatherMain; }
    public double getHumidity() { return humidity; }
    public double getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
}


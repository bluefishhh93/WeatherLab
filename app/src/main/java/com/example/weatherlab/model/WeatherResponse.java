package com.example.weatherlab.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private Weather[] weather;

    @SerializedName("name")
    private String name;

    @SerializedName("wind")
    private Wind wind;


    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public String getName() {
        return name;
    }

    public Wind getWind() {
        return wind;
    }


    public static class Main {
        @SerializedName("temp")
        private float temp;

        @SerializedName("humidity")
        private float humidity;

        @SerializedName("pressure")
        private float pressure;

        public float getTemp() {
            return temp;
        }

        public float getHumidity() {
            return humidity;
        }

        public float getPressure() {
            return pressure;
        }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        @SerializedName("main")
        private String main;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }

        public String getMain() {
            return main;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private float speed;

        public float getSpeed() {
            return speed;
        }
    }


}

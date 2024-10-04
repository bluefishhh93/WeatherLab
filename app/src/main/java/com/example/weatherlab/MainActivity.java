package com.example.weatherlab;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.weatherlab.model.ApiConfig;
import com.example.weatherlab.model.WeatherResponse;
import com.example.weatherlab.model.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText etCity;
    private TextView tvCity;
    private Button btnGetWeather;
    private TextView tvTemperature;
    private TextView tvDescription;
    private ImageView ivWeatherIcon;
    private ImageView ivBackground;
    private LinearLayout llAdditionalInfo;
    private Button btnPlaySound;


    private WeatherService weatherService;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkLocationPermission()) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }

        etCity = findViewById(R.id.etCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        ivBackground = findViewById(R.id.ivBackground);
        tvCity = findViewById(R.id.tvCity);
        llAdditionalInfo = findViewById(R.id.llAdditionalInfo);
//        btnPlaySound = findViewById(R.id.btnPlaySound);
        setDefaultBackground();

        Retrofit retrofit = new Retrofit.Builder()

                .baseUrl(ApiConfig.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                                .build();

        weatherService = retrofit.create(WeatherService.class);

//        btnPlaySound.setOnClickListener(v -> playWeatherSound());

        playDefaultSound();

        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setDefaultBackground() {
        Glide.with(this)
                .load(R.drawable.spalsh_screen)
                .into(ivBackground);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Using default location.", Toast.LENGTH_SHORT).show();
                getWeatherData("Ha Noi"); // Use a default city
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            getWeatherDataByCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get location. Using default.", Toast.LENGTH_SHORT).show();
                            getWeatherData("London"); // Use a default city
                        }
                    }
                });
    }

    private void getWeatherDataByCoordinates(double latitude, double longitude) {
        Call<WeatherResponse> call = weatherService.getWeatherByCoordinates(latitude, longitude, "metric", ApiConfig.API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    updateUI(weatherResponse);
                    playWeatherSound();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getWeatherData(String city){
        Call<WeatherResponse> call = weatherService.getWeather(city, "metric", ApiConfig.API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    updateUI(weatherResponse);
                    playWeatherSound();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherResponse weatherResponse) {
        tvCity.setText(weatherResponse.getName());
        tvTemperature.setText(String.format("%.1f°C", weatherResponse.getMain().getTemp()));
        tvDescription.setText(weatherResponse.getWeather()[0].getDescription());

        String iconUrl = "https://openweathermap.org/img/w/" + weatherResponse.getWeather()[0].getIcon() + ".png";
        Glide.with(this).load(iconUrl).into(ivWeatherIcon);

        updateBackground(weatherResponse.getWeather()[0].getMain());

        updateAdditionalInfo(weatherResponse);
    }

    private void updateBackground(String weatherMain) {
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
        Glide.with(this).load(backgroundResId).into(ivBackground);
    }

    private void updateAdditionalInfo(WeatherResponse weatherResponse) {
        if (weatherResponse.getMain() != null) {
            updateInfoItem(0, R.drawable.humidity,
                    String.format("%.0f%%", weatherResponse.getMain().getHumidity()), "Humidity");
            updateInfoItem(2, R.drawable.pressure,
                    String.format("%.0f hPa", weatherResponse.getMain().getPressure()), "Pressure");
        }
        if (weatherResponse.getWind() != null) {
            updateInfoItem(1, R.drawable.wind,
                    String.format("%.1f m/s", weatherResponse.getWind().getSpeed()), "Wind Speed");
        }
    }

    private void updateInfoItem(int index, int iconResId, String value, String label) {
        if (llAdditionalInfo != null && index < llAdditionalInfo.getChildCount()) {
            View infoItem = llAdditionalInfo.getChildAt(index);
            if (infoItem != null) {
                ImageView ivInfoIcon = infoItem.findViewById(R.id.ivInfoIcon);
                TextView tvInfoValue = infoItem.findViewById(R.id.tvInfoValue);
                TextView tvInfoLabel = infoItem.findViewById(R.id.tvInfoLabel);

                if (ivInfoIcon != null) ivInfoIcon.setImageResource(iconResId);
                if (tvInfoValue != null) tvInfoValue.setText(value);
                if (tvInfoLabel != null) tvInfoLabel.setText(label);
            }
        }
    }

    private void playWeatherSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        int soundResId;
        String weatherMain = tvDescription.getText().toString().toLowerCase();
        if (weatherMain.contains("rain") || weatherMain.contains("drizzle")) {
            soundResId = R.raw.rain_sound;
        } else if (weatherMain.contains("thunder")) {
            soundResId = R.raw.thunder_sound;
        } else if (weatherMain.contains("wind")) {
            soundResId = R.raw.wind_sound;
        } else if (weatherMain.contains("clear")) {
            soundResId = R.raw.clear_sound;
        } else if (weatherMain.contains("cloud")) {
            soundResId = R.raw.cloud_sound;
        } else {
            soundResId = R.raw.default_sound;
        }

        mediaPlayer = MediaPlayer.create(this, soundResId);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            Toast.makeText(this, "Error playing weather sound", Toast.LENGTH_SHORT).show();
        }
    }

    private void playDefaultSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        int soundResId = R.raw.default_sound; // Make sure you have a default_sound.mp3 in res/raw

        mediaPlayer = MediaPlayer.create(this, soundResId);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            Toast.makeText(this, "Error playing default sound", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
package com.example.weatherlab;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weatherlab.api.ApiConfig;
import com.example.weatherlab.model.WeatherResponse;
import com.example.weatherlab.model.WeatherService;
import com.example.weatherlab.utils.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private WeatherService weatherService;
    private LocationManager locationManager;
    private AuthManager authManager;
    private AudioManager audioManager;
    private UIManager uiManager;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    authManager.handleSignInResult(result.getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeServices();
        initializeManagers();
        setupClickListeners();
        setupWindowInsets();

        if (locationManager.checkLocationPermission()) {
            locationManager.getCurrentLocation();
        } else {
            locationManager.requestLocationPermission();
        }

        audioManager.playDefaultSound();
    }

    private void initializeServices() {
        FirebaseApp.initializeApp(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherService.class);
    }

    private void initializeManagers() {
        locationManager = new LocationManager(this);
        locationManager.setLocationCallback(new LocationManager.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                getWeatherDataByCoordinates(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onLocationError(String error) {
                uiManager.showToast(error);
                getWeatherData("London"); // Default city
            }
        });

        authManager = new AuthManager(this);
        authManager.setAuthCallback(new AuthManager.AuthCallback() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                // Update UI with user info
                if (user != null && user.getPhotoUrl() != null) {
                    uiManager.updateUserProfile(
                            user.getDisplayName(),
                            user.getEmail(),
                            user.getPhotoUrl().toString()
                    );
                    uiManager.showToast("Signed in successfully!");
                }
            }

            @Override
            public void onAuthError(String error) {
                uiManager.showToast("Authentication failed: " + error);
            }
        });

        // Add sign out button listener
//        findViewById(R.id.signOutButton).setOnClickListener(v -> {
//            authManager.signOut(task -> {
//                uiManager.clearUserProfile();
//                uiManager.showToast("Signed out successfully!");
//            });
//        });

        audioManager = new AudioManager(this);
        uiManager = new UIManager(this);
    }

    private void setupClickListeners() {
        findViewById(R.id.signInButton).setOnClickListener(v ->
                authManager.signIn(signInLauncher));

        uiManager.setGetWeatherClickListener(v -> {
            String city = uiManager.getCityInput();
            if (!city.isEmpty()) {
                getWeatherData(city);
            } else {
                uiManager.showToast("Please enter a city name");
            }
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });
    }

    private void getWeatherData(String city) {
        weatherService.getWeather(city, "metric", ApiConfig.API_KEY)
                .enqueue(new WeatherCallback());
    }

    private void getWeatherDataByCoordinates(double latitude, double longitude) {
        weatherService.getWeatherByCoordinates(latitude, longitude, "metric", ApiConfig.API_KEY)
                .enqueue(new WeatherCallback());
    }

    private class WeatherCallback implements Callback<WeatherResponse> {
        @Override
        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                WeatherResponse weatherResponse = response.body();
                uiManager.updateUI(weatherResponse);
                audioManager.playWeatherSound(weatherResponse.getWeather()[0].getMain());
            } else {
                uiManager.showToast("Error: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<WeatherResponse> call, Throwable t) {
            uiManager.showToast("Error: " + t.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.release();
    }
}
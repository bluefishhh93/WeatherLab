package com.example.weatherlab;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.databinding.DataBindingUtil;

import com.example.weatherlab.api.ApiConfig;
import com.example.weatherlab.databinding.ActivityMainBinding;
import com.example.weatherlab.model.UserData;
import com.example.weatherlab.model.WeatherData;
import com.example.weatherlab.model.WeatherService;
import com.example.weatherlab.repository.WeatherRepository;
import com.example.weatherlab.utils.AudioManager;
import com.example.weatherlab.utils.AuthManager;
import com.example.weatherlab.utils.LocationManager;
import com.example.weatherlab.utils.UIManager;
import com.example.weatherlab.viewmodel.WeatherViewModel;
import com.example.weatherlab.viewmodel.WeatherViewModelFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private WeatherViewModel weatherViewModel;
    private LocationManager locationManager;
    private AuthManager authManager;
    private AudioManager audioManager;
    private UIManager uiManager;
    private ActivityMainBinding binding;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        initializeServices();
        initializeManagers();
        setupClickListeners();
        setupWindowInsets();
        setupObservers();

        if (locationManager.checkLocationPermission()) {
            locationManager.getCurrentLocation();
        } else {
            locationManager.requestLocationPermission();
        }

        audioManager.playDefaultSound();
    }

    private void initializeServices() {
        FirebaseApp.initializeApp(this);
        WeatherService weatherService = ApiConfig.createWeatherService();
        WeatherRepository repository = new WeatherRepository(weatherService);
        WeatherViewModelFactory factory = new WeatherViewModelFactory(repository);
        weatherViewModel = new ViewModelProvider(this, factory).get(WeatherViewModel.class);
        binding.setViewModel(weatherViewModel);
    }

    private void initializeManagers() {
        locationManager = new LocationManager(this);
        locationManager.setLocationCallback(new LocationManager.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                weatherViewModel.getWeatherByLocation(location);
            }

            @Override
            public void onLocationError(String error) {
                uiManager.showToast(error);
                weatherViewModel.getWeatherByCity("London"); // Default city
            }
        });

        authManager = new AuthManager(this);
        authManager.setAuthCallback(new AuthManager.AuthCallback() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                if (user != null && user.getPhotoUrl() != null) {
                    UserData userData = new UserData(
                            user.getDisplayName(),
                            user.getEmail(),
                            user.getPhotoUrl().toString()
                    );
                    binding.setUserData(userData);
                    uiManager.showToast("Signed in successfully!");
                }
            }

            @Override
            public void onAuthError(String error) {
                uiManager.showToast("Authentication failed: " + error);
            }
        });

        audioManager = new AudioManager(this);
        uiManager = new UIManager(this, binding);
    }

    private void setupClickListeners() {
        binding.signInButton.setOnClickListener(v ->
                authManager.signIn(signInLauncher));

        binding.btnGetWeather.setOnClickListener(v -> {
            String city = binding.etCity.getText().toString().trim();
            if (!city.isEmpty()) {
                weatherViewModel.getWeatherByCity(city);
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

    private void setupObservers() {
        weatherViewModel.getWeatherData().observe(this, this::updateUI);
        weatherViewModel.getError().observe(this, uiManager::showToast);
        weatherViewModel.isLoading().observe(this, isLoading -> {
            // Update UI loading state
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        weatherViewModel.getCurrentWeatherSound().observe(this, audioManager::playWeatherSound);
    }

    private void updateUI(WeatherData weatherData) {
        binding.setWeatherData(weatherData);
        uiManager.updateBackground(weatherData.getWeatherMain());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.release();
    }
}
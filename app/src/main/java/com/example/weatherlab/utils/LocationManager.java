package com.example.weatherlab.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationManager {
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public interface LocationCallback {
        void onLocationReceived(Location location);
        void onLocationError(String error);
    }

    public LocationManager(Activity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void setLocationCallback(LocationCallback callback) {
        this.locationCallback = callback;
    }

    public boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void getCurrentLocation() {
        if (!checkLocationPermission()) {
            locationCallback.onLocationError("Location permission not granted");
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            locationCallback.onLocationReceived(location);
                        } else {
                            locationCallback.onLocationError("Unable to get location");
                        }
                    })
                    .addOnFailureListener(e ->
                            locationCallback.onLocationError("Error getting location: " + e.getMessage()));
        } catch (SecurityException e) {
            locationCallback.onLocationError("Security exception: " + e.getMessage());
        }
    }
}
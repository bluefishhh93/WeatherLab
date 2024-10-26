package com.example.weatherlab.repository;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationRepository {
    private final FusedLocationProviderClient fusedLocationClient;
    private final MutableLiveData<Location> locationData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LocationRepository(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            locationData.setValue(location);
                        } else {
                            error.setValue("Location not available");
                        }
                    })
                    .addOnFailureListener(e -> error.setValue(e.getMessage()));
        } catch (SecurityException e) {
            error.setValue("Location permission not granted");
        }
    }

    public MutableLiveData<Location> getLocationData() {
        return locationData;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
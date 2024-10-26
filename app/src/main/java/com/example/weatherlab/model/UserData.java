package com.example.weatherlab.model;

public class UserData {
    private String displayName;
    private String email;
    private String photoUrl;

    public UserData(String displayName, String email, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    // Getters
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }
}
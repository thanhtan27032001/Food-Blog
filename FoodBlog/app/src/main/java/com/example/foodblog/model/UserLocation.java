package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

public class UserLocation {
    private int userId;
    private String fullName;
    private String email;
    private String avatar;
    private String currentLocation;
    private Double latitude;
    @SerializedName("longtitude")
    private Double longitude;
    private String locationLastUpdated;

    // get all user location
    public UserLocation(int userId, String fullName, String email, String avatar, String currentLocation, Double latitude, Double longitude, String locationLastUpdated) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.avatar = avatar;
        this.currentLocation = currentLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationLastUpdated = locationLastUpdated;
    }

    // update my location


    public UserLocation(String currentLocation, Double latitude, Double longitude) {
        this.currentLocation = currentLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationLastUpdated() {
        return locationLastUpdated;
    }

    public String getAvatar() {
        return avatar;
    }
}

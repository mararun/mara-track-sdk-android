package com.mararun.runservice.sample.model;

import com.google.gson.annotations.SerializedName;

/**
 * created by mararun
 */

public class LapEndPoint {

    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

    public LapEndPoint() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

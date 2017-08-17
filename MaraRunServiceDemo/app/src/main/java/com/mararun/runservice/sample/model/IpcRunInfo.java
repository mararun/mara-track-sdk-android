package com.mararun.runservice.sample.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * created by mararun
 */

public class IpcRunInfo implements Parcelable {
    private double distance;
    private long duration;
    private long lapPace;
    private long pace;
    private double lat;
    private double lon;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.getDistance());
        dest.writeLong(this.getDuration());
        dest.writeLong(this.getLapPace());
        dest.writeLong(this.getPace());
        dest.writeDouble(this.getLat());
        dest.writeDouble(this.getLon());
    }

    public IpcRunInfo() {
    }

    protected IpcRunInfo(Parcel in) {
        this.setDistance(in.readDouble());
        this.setDuration(in.readLong());
        this.setLapPace(in.readLong());
        this.setPace(in.readLong());
        this.setLat(in.readDouble());
        this.setLon(in.readDouble());
    }

    public static final Creator<IpcRunInfo> CREATOR = new Creator<IpcRunInfo>() {
        @Override
        public IpcRunInfo createFromParcel(Parcel source) {
            return new IpcRunInfo(source);
        }

        @Override
        public IpcRunInfo[] newArray(int size) {
            return new IpcRunInfo[size];
        }
    };

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLapPace() {
        return lapPace;
    }

    public void setLapPace(long lapPace) {
        this.lapPace = lapPace;
    }

    public long getPace() {
        return pace;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}

package com.mararun.runservice.sample.model;

import com.google.gson.annotations.SerializedName;


/**
 * created by mararun
 */

public class LapDetail {
    @SerializedName("asc")
    private double asc;
    @SerializedName("dec")
    private double dec;
    @SerializedName("distance")
    private double distance;
    @SerializedName("duration")
    private double duration;
    @SerializedName("endpoint")
    private LapEndPoint endpoint;
    @SerializedName("lap_no")
    private double lapNo;
    @SerializedName("max_pace")
    private double maxPace;
    @SerializedName("pace")
    private double pace;
    @SerializedName("start_time")
    private double startTime;

    /**
     * calculated properties
     */
    private transient int accumulatedTime;
    private transient int paceDiff;
    private transient boolean isIncomplete;
    private transient int segmentAccumulatedTime;
    private transient int segmentPace;
    private transient int segmentPaceDiff;

    private transient float paceMaxPercent;
    public double getAsc() {
        return asc;
    }

    public void setAsc(double asc) {
        this.asc = asc;
    }

    public double getDec() {
        return dec;
    }

    public void setDec(double dec) {
        this.dec = dec;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public LapEndPoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(LapEndPoint endpoint) {
        this.endpoint = endpoint;
    }

    public double getLapNo() {
        return lapNo;
    }

    public void setLapNo(double lapNo) {
        this.lapNo = lapNo;
    }

    public double getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(double maxPace) {
        this.maxPace = maxPace;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public int getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(int accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }


    public int getPaceDiff() {
        return paceDiff;
    }

    public void setPaceDiff(int paceDiff) {
        this.paceDiff = paceDiff;
    }

    public boolean isIncomplete() {
        return isIncomplete;
    }

    public void setIncomplete(boolean incomplete) {
        isIncomplete = incomplete;
    }

    public float getPaceMaxPercent() {
        return paceMaxPercent;
    }

    public void setPaceMaxPercent(float paceMaxPercent) {
        this.paceMaxPercent = paceMaxPercent;
    }

    public int getSegmentAccumulatedTime() {
        return segmentAccumulatedTime;
    }

    public void setSegmentAccumulatedTime(int segmentAccumulatedTime) {
        this.segmentAccumulatedTime = segmentAccumulatedTime;
    }

    public int getSegmentPace() {
        return segmentPace;
    }

    public void setSegmentPace(int segmentPace) {
        this.segmentPace = segmentPace;
    }

    public int getSegmentPaceDiff() {
        return segmentPaceDiff;
    }

    public void setSegmentPaceDiff(int segmentPaceDiff) {
        this.segmentPaceDiff = segmentPaceDiff;
    }
}

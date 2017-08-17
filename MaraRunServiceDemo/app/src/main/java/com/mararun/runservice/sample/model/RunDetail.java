package com.mararun.runservice.sample.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * created by mararun
 */
public class RunDetail {
    public static final int FLAG_OK = 0;

    @SerializedName("altitude_down")
    private double altitudeDown;
    @SerializedName("altitude_up")
    private double altitudeUp;
    @SerializedName("avr_pace")
    private double avrPace;
    @SerializedName("avr_step_freq")
    private double avrStepFreq;
    @SerializedName("avr_step_length")
    private double avrStepLength;
    @SerializedName("distance")
    private double distance;
    @SerializedName("duration")
    private double duration;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("environment")
    private int environment;
    @SerializedName("halted_duration")
    private double haltedDuration;
    @SerializedName("init_time")
    private long initTime;
    @SerializedName("lapinfo")
    private LapsInfo lapinfo;
    @SerializedName("max_altitude")
    private double maxAltitude;
    @SerializedName("max_pace")
    private double maxPace;
    @SerializedName("max_step_freq")
    private double maxStepFreq;
    @SerializedName("max_step_length")
    private double maxStepLength;
    @SerializedName("min_altitude")
    private double minAltitude;
    @SerializedName("min_pace")
    private double minPace;
    @SerializedName("min_step_freq")
    private double minStepFreq;
    @SerializedName("min_step_length")
    private double minStepLength;
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("step_info")
    private List<StepInfo> stepInfo;
    @SerializedName("total_step_num")
    private double totalStepNum;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("trackpoints")
    private List<TrackPoint> trackpoints;
    @SerializedName("equipments")
    private List<String> equipments;
    @SerializedName("cheat_flag")
    private int cheatFlag;
    @SerializedName("faint_distance")
    private double faintDistance;
    @SerializedName("faint_duration")
    private double faintDuration;
    @SerializedName("sensor_cheat_flag")
    private double sensorCheatFlag;

    /**
     * ctor
     */
    public RunDetail() {
        //default ctor,
        // 默认空构造
    }

    public double getSensorCheatFlag() {
        return sensorCheatFlag;
    }

    public void setSensorCheatFlag(double sensorCheatFlag) {
        this.sensorCheatFlag = sensorCheatFlag;
    }

    public double getAltitudeDown() {
        return altitudeDown;
    }

    public void setAltitudeDown(double altitudeDown) {
        this.altitudeDown = altitudeDown;
    }

    public double getAltitudeUp() {
        return altitudeUp;
    }

    public void setAltitudeUp(double altitudeUp) {
        this.altitudeUp = altitudeUp;
    }

    public double getAvrPace() {
        return avrPace;
    }

    public void setAvrPace(double avrPace) {
        this.avrPace = avrPace;
    }

    public double getAvrStepFreq() {
        return avrStepFreq;
    }

    public void setAvrStepFreq(double avrStepFreq) {
        this.avrStepFreq = avrStepFreq;
    }

    public double getAvrStepLength() {
        return avrStepLength;
    }

    public void setAvrStepLength(double avrStepLength) {
        this.avrStepLength = avrStepLength;
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

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getHaltedDuration() {
        return haltedDuration;
    }

    public void setHaltedDuration(double haltedDuration) {
        this.haltedDuration = haltedDuration;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public LapsInfo getLapinfo() {
        return lapinfo;
    }

    public void setLapinfo(LapsInfo lapinfo) {
        this.lapinfo = lapinfo;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public double getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(double maxPace) {
        this.maxPace = maxPace;
    }

    public double getMaxStepFreq() {
        return maxStepFreq;
    }

    public void setMaxStepFreq(double maxStepFreq) {
        this.maxStepFreq = maxStepFreq;
    }

    public double getMaxStepLength() {
        return maxStepLength;
    }

    public void setMaxStepLength(double maxStepLength) {
        this.maxStepLength = maxStepLength;
    }

    public double getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    public double getMinPace() {
        return minPace;
    }

    public void setMinPace(double minPace) {
        this.minPace = minPace;
    }

    public double getMinStepFreq() {
        return minStepFreq;
    }

    public void setMinStepFreq(double minStepFreq) {
        this.minStepFreq = minStepFreq;
    }

    public double getMinStepLength() {
        return minStepLength;
    }

    public void setMinStepLength(double minStepLength) {
        this.minStepLength = minStepLength;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public List<StepInfo> getStepInfo() {
        return stepInfo;
    }

    public void setStepInfo(List<StepInfo> stepInfo) {
        this.stepInfo = stepInfo;
    }

    public double getTotalStepNum() {
        return totalStepNum;
    }

    public void setTotalStepNum(double totalStepNum) {
        this.totalStepNum = totalStepNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<TrackPoint> getTrackpoints() {
        return trackpoints;
    }

    public void setTrackpoints(List<TrackPoint> trackpoints) {
        this.trackpoints = trackpoints;
    }

    public List<String> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<String> equipments) {
        this.equipments = equipments;
    }

    public int getCheatFlag() {
        return cheatFlag;
    }

    public void setCheatFlag(int cheatFlag) {
        this.cheatFlag = cheatFlag;
    }

    public double getFaintDuration() {
        return faintDuration;
    }

    public void setFaintDuration(double faintDuration) {
        this.faintDuration = faintDuration;
    }

    public double getFaintDistance() {
        return faintDistance;
    }

    public void setFaintDistance(double faintDistance) {
        this.faintDistance = faintDistance;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }
}

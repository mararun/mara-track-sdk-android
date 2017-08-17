package com.mararun.runservice.sample.model;

import com.google.gson.annotations.SerializedName;

/**
 * created by mararun
 */

public class StepInfo {

    @SerializedName("distance")
    public double distance;

    @SerializedName("duration")
    public double duration;

    @SerializedName("seq_no")
    public int seqNo;

    @SerializedName("step")
    public int step;

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

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}

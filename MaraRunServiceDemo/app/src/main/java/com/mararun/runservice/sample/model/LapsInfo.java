package com.mararun.runservice.sample.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * created by mararun
 */

public class LapsInfo {
    @SerializedName("lap_num")
    private double lapNum;
    @SerializedName("lap_detail")
    private List<LapDetail> lapDetail;

    public double getLapNum() {
        return lapNum;
    }

    public void setLapNum(double lapNum) {
        this.lapNum = lapNum;
    }

    public List<LapDetail> getLapDetail() {
        return lapDetail;
    }

    public void setLapDetail(List<LapDetail> lapDetail) {
        this.lapDetail = lapDetail;
    }
}

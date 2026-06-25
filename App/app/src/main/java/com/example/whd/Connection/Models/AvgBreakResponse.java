package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class AvgBreakResponse {
    @SerializedName("avg_break_minutes")
    private double avgBreakMinutes;
    public AvgBreakResponse() {}
    public double getAvgBreakMinutes() { return avgBreakMinutes; }


}
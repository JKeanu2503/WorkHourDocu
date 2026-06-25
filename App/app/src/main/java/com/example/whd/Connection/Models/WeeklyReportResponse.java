package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class WeeklyReportResponse {
    @SerializedName("week")
    private String week;
    @SerializedName("worked_hours")
    private double workedHours;
    @SerializedName("target_hours")
    private double targetHours;
    public WeeklyReportResponse() {}

    public String getWeek() {
        return week;
    }

    public double getWorkedHours() {
        return workedHours;
    }

    public double getTargetHours() {
        return targetHours;
    }
}
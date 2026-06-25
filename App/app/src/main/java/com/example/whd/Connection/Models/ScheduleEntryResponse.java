package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class ScheduleEntryResponse {
    @SerializedName("weekly_hours_target")
    private double weeklyHoursTarget;
    @SerializedName("valid_from")
    private String validFrom;
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    public ScheduleEntryResponse() {}

    public double getWeeklyHoursTarget() {
        return weeklyHoursTarget;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }
}
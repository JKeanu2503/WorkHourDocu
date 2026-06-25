package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class WorkEntryResponse {
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("break_minutes")
    private int breakMinutes;
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    public WorkEntryResponse() {}

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }
}
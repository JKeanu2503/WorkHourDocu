package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class WorkUpdate {
    private String start_time;
    private String end_time;
    private Integer break_minutes; // Integer damit null möglich ist
    public WorkUpdate(String start, String end, Integer breakMin) {
        this.start_time = start;
        this.end_time = end;
        this.break_minutes = breakMin;
    }
}
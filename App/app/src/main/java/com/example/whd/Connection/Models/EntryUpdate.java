package com.example.whd.Connection.Models;

public class EntryUpdate {
    private String start_time;
    private String end_time;
    private Integer break_minutes;
    public EntryUpdate(String start, String end, Integer breakMin) {
        this.start_time = start;
        this.end_time = end;
        this.break_minutes = breakMin;
    }
}
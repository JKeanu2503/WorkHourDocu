package com.example.whd.Connection.Models;

public class WorkCreate {
    private String start_time;
    private String end_time;
    private int break_minutes;

    public WorkCreate (String start, String end, int breakMin) {
        this.start_time = start;
        this.end_time = end;
        this.break_minutes = breakMin;
    }
}

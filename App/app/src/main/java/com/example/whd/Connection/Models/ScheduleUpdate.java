package com.example.whd.Connection.Models;

public class ScheduleUpdate {
    private double weekly_hours_target;
    private String valid_from;
    public ScheduleUpdate(double target, String from) {
        this.weekly_hours_target = target;
        this.valid_from = from;
    }
}
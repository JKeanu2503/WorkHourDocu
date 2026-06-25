package com.example.whd.Connection.Models;

public class ScheduleCreate {

    private double weekly_hours_target;

    private String valid_from;

    public ScheduleCreate (double weekly_hours_target, String valid_from) {
        this.valid_from = valid_from;
        this.weekly_hours_target = weekly_hours_target;
    }

}

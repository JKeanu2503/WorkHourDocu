package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class ComparisonResponse {
    @SerializedName("worked")
    private double worked;
    @SerializedName("target")
    private double target;

    public ComparisonResponse() {}

    public double getWorked() { return worked; }
    public double getTarget() { return target; }
}
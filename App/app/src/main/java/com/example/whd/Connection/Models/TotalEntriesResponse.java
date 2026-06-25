package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class TotalEntriesResponse {
    @SerializedName("total_entries")
    private int totalEntries;
    public TotalEntriesResponse() {}
    public int getTotalEntries() { return totalEntries; }
}
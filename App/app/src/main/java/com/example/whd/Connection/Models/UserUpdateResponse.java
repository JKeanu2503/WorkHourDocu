package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class UserUpdateResponse {
    @SerializedName("username")
    private String username;
    @SerializedName("id")
    private int id;
    public UserUpdateResponse() {}
    public String getUsername() { return username; }
    public int getId() { return id; }
}
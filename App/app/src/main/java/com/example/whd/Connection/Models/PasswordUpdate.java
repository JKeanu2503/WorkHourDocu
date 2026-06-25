package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class PasswordUpdate {
    @SerializedName("old_password")
    private String old_password;
    @SerializedName("new_password")
    private String new_password;
    public PasswordUpdate(String old, String newP) {
        this.old_password = old;
        this.new_password = newP;
    }
}
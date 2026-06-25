package com.example.whd.Connection.Models;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;
    public TokenResponse() {}
    public String getAccessToken() { return accessToken; }
}
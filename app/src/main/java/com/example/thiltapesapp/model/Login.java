package com.example.thiltapesapp.model;

import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() { return accessToken; }
    // Getters
}

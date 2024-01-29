package com.zin.dvac;

import com.google.gson.annotations.SerializedName;

public class PasswordResetResponse {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}

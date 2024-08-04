package com.example.omarassignment3;

import com.google.gson.annotations.SerializedName;

public class BioRequest {
    @SerializedName("email")
    private String email;

    public BioRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.example.omarassignment3;

import com.google.gson.annotations.SerializedName;

public class PicRequest {
    @SerializedName("email")
    private String email;
    public PicRequest(String email) {
        this.email = email;
    }
    public String getEmail(){return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}



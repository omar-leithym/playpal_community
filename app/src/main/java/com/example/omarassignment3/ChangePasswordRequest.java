package com.example.omarassignment3;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("oldPassword")
    private String oldPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public ChangePasswordRequest(String email, String oldPassword, String newPassword) {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}

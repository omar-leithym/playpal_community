package com.example.omarassignment3;

import com.google.gson.annotations.SerializedName;

public class RemoveFriendRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("email2")
    private String email2;

    public RemoveFriendRequest(String email, String email2) {
        this.email = email;
        this.email2 = email2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }
}

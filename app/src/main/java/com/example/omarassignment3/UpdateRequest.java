package com.example.omarassignment3;

import com.google.gson.annotations.SerializedName;

public class UpdateRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("bio")
    private String bio;

    @SerializedName("profilePicURL")
    private String profilePicURL;

    public UpdateRequest(String email, String bio, String profilePicURL) {
        this.email = email;
        this.bio = bio;
        this.profilePicURL = profilePicURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}

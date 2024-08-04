package com.example.omarassignment3;

public class PicResponse {
    private String profilePicURL; // Ensure this matches your API response field name

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    @Override
    public String toString() {
        return "PicResponse{" +
                "profilePicURL='" + profilePicURL + '\'' +
                '}';
    }
}

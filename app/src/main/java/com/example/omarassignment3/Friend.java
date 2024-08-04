package com.example.omarassignment3;

public class Friend {
    private String email;
    private String name;
    private String profilePicUrl;
    private String lastMessage;

    public Friend(String email, String name, String profilePicUrl, String lastMessage) {
        this.email = email;
        this.name = name;
        this.profilePicUrl = profilePicUrl;
        this.lastMessage = lastMessage;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

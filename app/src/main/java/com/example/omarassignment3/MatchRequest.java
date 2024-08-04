package com.example.omarassignment3;

public class MatchRequest {
    private int id;
    private int requester_id;
    private int requested_id;
    private String status;
    private String timestamp;
    private String profilePicURL; // URL for the profile image
    private String firstName;       // First name of the requester
    private String lastName;
    private String otherEmail;// Last name of the requester

    // Constructor
    public MatchRequest(int id, int requester_id, int requested_id, String status, String timestamp, String profilePicURL, String firstName, String lastName, String otherEmail) {
        this.id = id;
        this.requester_id = requester_id;
        this.requested_id = requested_id;
        this.status = status;
        this.timestamp = timestamp;
        this.profilePicURL = profilePicURL;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherEmail = otherEmail;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequesterId() {
        return requester_id;
    }

    public void setRequesterId(int requester_id) {
        this.requester_id = requester_id;
    }

    public int getRequestedId() {
        return requested_id;
    }

    public void setRequestedId(int requested_id) {
        this.requested_id = requested_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImageUrl() {
        return profilePicURL;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profilePicURL = profileImageUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOtherEmail() {
        return otherEmail;
    }
}



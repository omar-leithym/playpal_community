package com.example.omarassignment3;


public class SendMatchResponse {
    private int id;
    private int requester_id;
    private int requested_id;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(int requester_id) {
        this.requester_id = requester_id;
    }

    public int getRequested_id() {
        return requested_id;
    }

    public void setRequested_id(int requested_id) {
        this.requested_id = requested_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
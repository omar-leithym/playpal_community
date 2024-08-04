package com.example.omarassignment3;

public class SendMatchRequest {
    private int id;
    private int targetID;

    public SendMatchRequest(int id, int targetID) {
        this.id = id;
        this.targetID = targetID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTargetID() {
        return targetID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
}

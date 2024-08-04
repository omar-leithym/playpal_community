package com.example.omarassignment3;

public class UploadPictureResponse {
    private boolean success;
    private String message;

    // Constructor
    public UploadPictureResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Default constructor (needed for Gson)
    public UploadPictureResponse() {
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

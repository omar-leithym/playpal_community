package com.example.omarassignment3;

public class ChatMessage {
    private String senderEmail;
    private String receiverEmail;
    private String message;
    private String timestamp;
    private boolean isDateSeparator;
    private String date;

    // Constructor for messages
    public ChatMessage(String senderEmail, String receiverEmail, String message, String timestamp) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.message = message;
        this.timestamp = timestamp;
        this.isDateSeparator = false;
    }

    // Constructor for date separators
    public ChatMessage(String date) {
        this.date = date;
        this.isDateSeparator = true;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isDateSeparator() {
        return isDateSeparator;
    }

    public String getDate() {
        return date;
    }
}

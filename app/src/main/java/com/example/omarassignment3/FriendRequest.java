package com.example.omarassignment3;

public class FriendRequest {
    private String email;
    private String email2;

    // Constructors, getters, and setters
    public FriendRequest(String email, String email2) {
        this.email = email;
        this.email2 = email2;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getemail2() {
        return email2;
    }

    public void setemail2(String email2) {
        this.email2 = email2;
    }
}

package com.example.omarassignment3;

public class Player {
    private String profilePicURL;
    private String firstName;
    private String lastName;
    private String level;
    private String email;
    private int id;

    public Player(String profilePicURL, String firstName, String lastName, String level, String email, int id) {
        if (profilePicURL == "null" || profilePicURL.isEmpty()) {
            this.profilePicURL = "https://static.vecteezy.com/system/resources/previews/005/544/718/non_2x/profile-icon-design-free-vector.jpg";
        } else {
            this.profilePicURL = profilePicURL;
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.level = level;
        this.email = email;
        this.id = id;
    }

    public String getImageUrl() {
        return profilePicURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getLevel() {
        return level;
    }

    public int getID() {
        return id;
    }
}



package com.example.omarassignment3;
public class AddSportRequest {
    private String sport_name;
    private String email;
    private String skillLevel;

    public AddSportRequest(String sport_name, String email, String skillLevel) {
        this.sport_name = sport_name;
        this.email = email;
        this.skillLevel = skillLevel;
    }

    // Getters and setters
    public String getSport_name() {
        return sport_name;
    }

    public void setSport_name(String sport_name) {
        this.sport_name = sport_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }
}
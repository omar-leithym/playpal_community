package com.example.omarassignment3;

import java.util.List;

public class ProfileResponse {
    private String username;
    private String bio;
    private List<String> urlPics;
    private List<String> favSports;
    private List<String> favSportsSkill;
    private String firstName;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getUrlPics() {
        return urlPics;
    }
    public List<String> getSports(){
        return favSports;
    }
    public List<String> getSkillLevelSport(){
        return favSportsSkill;
    }
    public String getName(){
        return firstName;
    }


    public void setUrlPics(List<String> urlPics) {
        this.urlPics = urlPics;
    }
}

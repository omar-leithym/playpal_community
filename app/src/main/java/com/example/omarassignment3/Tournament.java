package com.example.omarassignment3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Tournament {
    private int tournamentID;
    private String tournamentName;
    private String sportName;
    private String startDate;
    private String endDate;
    private int teamSize;
    private String location;
    private int totalTeams;
    private int remainingTeams;
    private int prizePool;

    public Tournament(String tournamentName, String sportName, String startDate, String endDate, int teamSize, String location,
                      int totalTeams, int remainingTeams, int prizePool, int tournamentID) {
        this.tournamentName = tournamentName;
        this.sportName = sportName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.teamSize = teamSize;
        this.location = location;
        this.totalTeams = totalTeams;
        this.remainingTeams = remainingTeams;
        this.prizePool=prizePool;
        this.tournamentID = tournamentID;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public String getSportName() {
        return sportName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public String getLocation() {
        return location;
    }

    public String getTimeLeft() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date tournamentStart = formatter.parse(startDate);
            Date now = new Date();

            long diffInMillis = tournamentStart.getTime() - now.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;

            if (days > 0) {
                return days + " days, " + hours + " hours, " + minutes + " minutes until start!";
            } else if (hours > 0) {
                return hours + " hours, " + minutes + " minutes until start!";
            } else {
                return minutes + " minutes until start!";
            }
        } catch (Exception e) {
            return "Invalid date format";
        }
    }

    public int getPrizePool() {
        return prizePool;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public int getRemainingTeams() {
        return remainingTeams;
    }

    public int getTotalTeams() {
        return totalTeams;
    }

}




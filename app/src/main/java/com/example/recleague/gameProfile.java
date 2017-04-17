package com.example.recleague;

import java.util.Date;

public class gameProfile {

    private String location;
    private String sport;
    private int playerLimit;
    private int currentPlayers;
    private String owner;
    private Date dateTime;

    public gameProfile(String loc, String sport, int limit, String owner, Date dateTime)
    {

        location = loc;
        this.sport = sport;
        playerLimit = limit;
        currentPlayers = 1;
        this.owner = owner;
        this.dateTime = dateTime;

    }
    public void setCurrentPlayers(int tmp)
    {
        currentPlayers = tmp;

    }
    public String getLocation()
    {
        return location;
    }
    public String getSport()
    {
        return sport;
    }
    public int getPlayerLimit()
    {
        return playerLimit;
    }
    public int getCurrentPlayers()
    {
        return currentPlayers;
    }
    public String getOwner() {
        return owner;
    }
    public Date getDateTime() {
        return dateTime;
    }



}

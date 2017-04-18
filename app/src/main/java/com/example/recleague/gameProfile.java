package com.example.recleague;

import java.util.Date;

public class gameProfile {

    public String location;
    public String sport;
    public int playerLimit;
    public int currentPlayers;
    public String owner;
    public Date dateTime;

    public gameProfile()
    {
        //default
    }
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

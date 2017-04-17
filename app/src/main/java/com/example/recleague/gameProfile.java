package com.example.recleague;

/**
 * Created by davidstern on 4/17/17.
 */

public class gameProfile {

    private String location;
    private String sport;
    private int playerlimit;
    private int currentplayers;
    private String owner;

    public gameProfile(String loc,String spor, int lim, int curlim, String own)
    {

        location = loc;
        sport = spor;
        playerlimit = lim;
        currentplayers = curlim;
        owner = own;

    }
    public String getLocation()
    {
        return location;
    }
    public String getSport()
    {
        return sport;
    }
    public int getPlayerlimit()
    {
        return playerlimit;
    }
    public int getCurrentplayers()
    {
        return currentplayers;
    }



}

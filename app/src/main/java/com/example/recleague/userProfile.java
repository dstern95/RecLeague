package com.example.recleague;

import java.util.ArrayList;

/**
 * Created by davidstern on 4/18/17.
 */

public class userProfile {

    private String nickname;
    private String username;
    private String userid;
    private double rating;
    private String bio;
    private ArrayList<String> raters;
    private ArrayList<String> joinedgames;

    public userProfile(){

        //needed for firebase
    }

    public userProfile(String username, String id)
    {
        nickname = username;
        this.username = username;
        userid =id;
        rating = 0;
        bio = "no bio written";



    }

    public void setBio(String nbio){
        bio = nbio;
    }
    public String getBio(){
        return bio;
    }

    public boolean hasRated(String rater)
    {

        if (raters == null)
        {
            raters = new ArrayList<>();
        }


        return !raters.contains(rater);
    }

    public void addRating(String rater,double urating)
    {
        if (raters == null)
        {
            raters = new ArrayList<>();
        }
        double totrate = rating*raters.size();
        totrate += urating;
        raters.add(rater);
        rating = totrate/raters.size();


    }

    public void removeGame(String game)
    {
        if (joinedgames == null)
        {
            joinedgames = new ArrayList<>();
        }
        else {
            joinedgames.remove(game);
        }
    }
    public void addGame(String game)
    {
        if (joinedgames == null)
        {
            joinedgames = new ArrayList<>();
        }
        joinedgames.add(game);
    }
    public boolean playingGame(String game)
    {
        if (joinedgames == null)
        {
            joinedgames = new ArrayList<>();
        }

        return joinedgames.contains(game);
    }

    public ArrayList<String> getJoinedgames()
    {

        return joinedgames;
    }
    public void setNickname(String newnickname){
        nickname = newnickname;
    }
    public double getRating(){
        return rating;
    }
    public String getUsername()
    {
        return username;
    }
    public String getNickname()
    {
        return nickname;
    }
    public String getUserid()
    {
        return userid;
    }
}

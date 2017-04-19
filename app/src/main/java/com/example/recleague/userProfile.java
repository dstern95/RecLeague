package com.example.recleague;

import java.util.ArrayList;

/**
 * Created by davidstern on 4/18/17.
 */

public class userProfile {

    String nickname;
    String username;
    String userid;
    double rating;
    ArrayList<String> raters;
    ArrayList<String> joinedgames;

    public userProfile(){

        //needed for firebase
    }

    public userProfile(String username, String id)
    {
        nickname = username;
        this.username = username;
        userid =id;
        rating = 0;



    }

    public void removeGame(String game)
    {
        joinedgames.remove(game);
    }
    public void addGame(String game)
    {
        joinedgames.add(game);
    }
    public ArrayList<String> getJoinedgames()
    {
        return joinedgames;
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
}

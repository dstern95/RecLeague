package com.example.recleague;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class gameProfile {

    public String location;
    public String sport;
    public int playerLimit;
    public int currentPlayers;
    public String owner;
    public Date dateTime;
    public ArrayList<String> going;
    public ArrayList<String> goingid;
    public String id;
    public String coordinates;
    private final String TAG = "gameProfile";


    public gameProfile()
    {
        //needed for firebase
    }

    public gameProfile(String loc, String sport, int limit, String owner, Date dateTime,String ownerid, String coord)
    {

        location = loc;
        this.sport = sport;
        playerLimit = limit;
        currentPlayers = 1;
        this.owner = owner;
        this.dateTime = dateTime;
        going = new ArrayList<String>();
        goingid = new ArrayList<String>();

        coordinates = coord;
        goingid.add(ownerid);
        going.add(owner);
        id = "";
        Random r = new Random();

        for (int i=0; i<21; i++)
        {
            int n = r.nextInt(74)+48;
            id+=Character.toString((char) n);
        }

    }
    public void addPlayer(String name,String uid)
    {
        if (going == null)
        {
            going = new ArrayList<String>();
        }

        if (goingid == null)
        {
            goingid = new ArrayList<String>();
        }

        going.add(name);
        goingid.add(uid);
        currentPlayers +=1;

    }
    public void removePlayer(String name)
    {
        going.remove(name);
        currentPlayers-=1;

    }
    public void setCurrentPlayers(int tmp)
    {
        currentPlayers = tmp;

    }

    public boolean isGoing(String name)
    {
        //name = name.replace(".", "~");
        for(int i =0;i<going.size();i++)
        {
            Log.d(TAG, "is looking  "+going.get(i)+" vs "+name);

            if (name.equals(going.get(i)))
            {
                Log.d(TAG, "is going  ");

                return true;
            }
        }
        return false;
    }

    public boolean isGoingid(String uid)
    {
        //name = name.replace(".", "~");
        for(int i =0;i<goingid.size();i++)
        {
            Log.d(TAG, "is looking  "+going.get(i)+" vs "+uid);

            if (uid.equals(goingid.get(i)))
            {
                Log.d(TAG, "is going  ");

                return true;
            }
        }
        return false;
    }


    public boolean isId(String tmp){

        if (id.equals(tmp))
        {
            return true;

        }
        return false;
    }

    public ArrayList<String> getGoingid(){
        return goingid;
    }

    public LatLng getLatLlng()
    {
        if (coordinates != null) {
            String loc = coordinates;
            loc = loc.replace("lat/lng: (", "");
            loc = loc.replace(")", "");
            loc = loc.replace(" ", "");
            String[] tmp = loc.split(",");
            //Log.d(TAG, tmp[0])
            return new LatLng(Double.valueOf(tmp[0]), Double.valueOf(tmp[1]));
        }
        LatLng tmp =null;
        return tmp;

    }

    public String getCoordinates()
    {
    return coordinates;
    }
    public String getId(){return id;}
    public ArrayList<String> getGoing(){
        return going;
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

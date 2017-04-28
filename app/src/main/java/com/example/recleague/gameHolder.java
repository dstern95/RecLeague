package com.example.recleague;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by davidstern on 4/17/17.
 */

public class gameHolder extends ArrayList<gameProfile>{

    private final String TAG = "gameHolder";

    public gameHolder(){

        super();

    }
    public gameHolder(ArrayList<gameProfile> tmp)
    {
        super();

        Calendar c = Calendar.getInstance();
        Date current = c.getTime();
        for(int i=0; i<tmp.size();i++)
        {
            Log.d(TAG, current.toString() +" found a match "+tmp.get(i).getDateTime().toString());

            if (tmp.get(i).getDateTime().after(current)) {
                this.add(tmp.get(i));
            }
        }
    }


    public gameProfile findbyId(String id)
    {
        for (int i=0; i<this.size();i++)
        {

            //Log.d(TAG, "found a match  ");

            if (this.get(i).isId(id))
            {
                //Log.d(TAG, "foud "+this.get(i).getId()+" vs "+id);


                return this.get(i);
            }
        }


        return null;
    }


    public boolean replace(gameProfile upprof)
    {
        int i = 0;
        while (i < this.size()) {
            if (this.get(i).getId().equals(upprof.getId())) {
                this.add(i,upprof);
                this.remove(i+1);
                return true;
            }
            i+=1;



        }
        return false;

    }


    public boolean insadd(gameProfile newprof)
    {

            int i = 0;
            while (i < this.size()) {
                    if (this.get(i).getDateTime().after(newprof.getDateTime())) {
                        this.add(i,newprof);
                        return true;
                    }
                    i+=1;

            }

            this.add(newprof);
        return true;

    }



}

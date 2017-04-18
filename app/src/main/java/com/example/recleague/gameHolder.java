package com.example.recleague;

import java.util.ArrayList;

/**
 * Created by davidstern on 4/17/17.
 */

public class gameHolder {

    gameProfile[] holder;

    int size;
    public gameHolder(){
        size = 0;
        holder = new gameProfile[1];
    }

    public gameProfile[] getHolder()
    {
        return holder;

    }

    public void add(gameProfile newprof)
    {
        boolean added = false;
        gameProfile[] tmpholder= new gameProfile[size+1];
        int i = 0;
        int cur = 0;
        while (i<size)
        {
            if (added == false)
            {
                if (holder[i].getDateTime().after(newprof.getDateTime()))
                {
                    tmpholder[cur] = newprof;
                    cur +=1;
                    added = true;
                }

            }
            tmpholder[cur] = holder[i];
            cur += 1;
            i += 1;
        }
        size +=1;
        holder = tmpholder;


    }



}

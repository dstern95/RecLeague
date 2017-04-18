package com.example.recleague;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidstern on 4/17/17.
 */

public class gameHolder extends ArrayList<gameProfile>{


    public gameHolder(){

        super();

    }
    public gameHolder(ArrayList<gameProfile> tmp)
    {
        super();

        for(int i=0; i<tmp.size();i++)
        {
            this.add(tmp.get(i));

        }
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

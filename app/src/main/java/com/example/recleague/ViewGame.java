package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewGame extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String user;
    private final String TAG = "viewgame";

    String gameid;
    gameHolder games;

    gameProfile selgame;

    int current;
    int max;
    int decision;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);
        Intent i = new Intent();
        Bundle extra = getIntent().getExtras();
        gameid = getIntent().getStringExtra("GameName");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        user = sharedpreferences.getString("user",null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {};
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);
                games = new gameHolder(tmp);
                update(false);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }

    public void joinorleave(View v)
    {
        if (decision == 1)
        {
            selgame.addPlayer(user);
            submit();
        }
        else if (decision == 2)
        {
            selgame.removePlayer(user);
            submit();
        }

    }

    public void submit()
    {

        games.replace(selgame);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");


        myRef.setValue(games);

        Toast.makeText(ViewGame.this, "Game updated",
                Toast.LENGTH_SHORT).show();


    }




    public void update(boolean orig){

        decision = 0;
        if (!orig)
        {
            Log.d(TAG, "here in update "+ gameid);

            selgame = games.findbyId(gameid);
            if (selgame != null)
            {
                Log.d(TAG, "selgame in update "+ selgame.getId());


                TextView tv_date = (TextView)findViewById(R.id.date_text);
                TextView tv_sport = (TextView)findViewById(R.id.sport_text);
                TextView tv_location = (TextView)findViewById(R.id.location_text);
                TextView tv_owner = (TextView)findViewById(R.id.owner_text);
                TextView tv_lim = (TextView)findViewById(R.id.lim_text);

                TextView tv_load = (TextView)findViewById(R.id.load_text);

                tv_load.setVisibility(View.INVISIBLE);

                tv_date.setText(selgame.getDateTime().toString());
                tv_date.setVisibility(View.VISIBLE);

                tv_owner.setText(selgame.getOwner());
                tv_owner.setVisibility(View.VISIBLE);

                tv_sport.setText(selgame.getSport());
                tv_sport.setVisibility(View.VISIBLE);

                tv_location.setText(selgame.getLocation());
                tv_location.setVisibility(View.VISIBLE);

                current = selgame.getCurrentPlayers();
                max = selgame.getPlayerLimit();

                String tmp ="";
                tmp += Integer.toString(current);
                tmp+="/";
                tmp+=Integer.toString(max);
                tv_lim.setText(tmp);
                tv_lim.setVisibility(View.VISIBLE);

                Button bt = (Button)findViewById(R.id.jorl_button);
                bt.setVisibility(View.VISIBLE);
                if(selgame.isGoing(user))
                {
                    decision = 2;
                    bt.setText("Leave Game");
                }
                else
                {
                    if (current<max)
                    {
                        decision =1;
                        bt.setText("Join Game");
                    }
                    else
                    {
                        decision = 0;
                        bt.setText("Game Full");
                    }
                }



            }

        }

    }
}

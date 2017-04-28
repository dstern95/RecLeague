package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    userProfile curUser;
    gameProfile selgame;
    String[] signedup;
    Bitmap[] images;

    int current;
    int max;
    int decision;

    LatLng coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);
        Intent i = new Intent();
        Bundle extra = getIntent().getExtras();
        gameid = getIntent().getStringExtra("GameName");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        user = sharedpreferences.getString("user",null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        Spinner s = (Spinner)findViewById(R.id.signeup);

        signedup = new String[1];
        signedup[0] = "None";
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, signedup);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        s.setAdapter(spinnerArrayAdapter);
        s.setOnItemSelectedListener(new ViewGame.playersListener());


        final FirebaseUser user2 = mAuth.getCurrentUser();



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {};
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);
                if (tmp != null) {
                    games = new gameHolder(tmp);
                    update(false);
                }
                else
                {
                    update(true);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        DatabaseReference usdata = database.getReference(user2.getUid());
        usdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<userProfile> t = new GenericTypeIndicator<userProfile>() {};
                userProfile tmp = dataSnapshot.getValue(t);
                curUser = tmp;

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
            selgame.addPlayer(user,curUser.getUserid());
            Log.d(TAG, "Look here "+ curUser.getUsername());

            curUser.addGame(selgame.getId());
            submit();
        }
        else if (decision == 2)
        {
            selgame.removePlayer(user);
            curUser.removeGame(selgame.getId());

            submit();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();


        Spinner s = (Spinner)findViewById(R.id.signeup);

        s.setSelection(0);


    }
    class playersListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            if (!signedup[pos].equals("None"))
            {
                Intent i = new Intent(ViewGame.this, ViewUserProfile.class);
                i.putExtra("userId", selgame.getGoingid().get(pos-1));
                startActivity(i);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Do nothing
        }
    }
    public void submit()
    {
        boolean exit = false;
        games.replace(selgame);
        if (selgame.getCurrentPlayers() ==0)
        {
            games.remove(selgame);
            exit = true;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");
        DatabaseReference myRef2 = database.getReference(curUser.getUserid());


        myRef.setValue(games);
        myRef2.setValue(curUser);


        Toast.makeText(ViewGame.this, "Game updated",
                Toast.LENGTH_SHORT).show();
        if (exit)
        {
            Log.d(TAG, "none left");

            finish();
        }

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
                TextView date = (TextView) findViewById(R.id.tv_date);

                TextView tv_sport = (TextView)findViewById(R.id.sport_text);
                TextView sport = (TextView) findViewById(R.id.tv_sport);

                TextView tv_location = (TextView)findViewById(R.id.location_text);
                TextView location = (TextView) findViewById(R.id.tv_location);
                Button mapButton = (Button) findViewById(R.id.open_maps);

                TextView tv_owner = (TextView)findViewById(R.id.owner_text);
                TextView owner = (TextView) findViewById(R.id.tv_owner);

                TextView tv_lim = (TextView)findViewById(R.id.lim_text);
                TextView lim = (TextView) findViewById(R.id.tv_limit);

                TextView tv_load = (TextView)findViewById(R.id.load_text);

                tv_load.setVisibility(View.INVISIBLE);

                tv_date.setText(selgame.getDateTime().toString());
                tv_date.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);


                tv_owner.setText(selgame.getOwner());
                tv_owner.setVisibility(View.VISIBLE);
                owner.setVisibility(View.VISIBLE);

                tv_sport.setText(selgame.getSport());
                tv_sport.setVisibility(View.VISIBLE);
                sport.setVisibility(View.VISIBLE);

                tv_location.setText(selgame.getLocation());
                tv_location.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);

                coordinates = selgame.getLatLlng();

                if (coordinates != null) {
                    mapButton.setVisibility(View.VISIBLE);
                }

                current = selgame.getCurrentPlayers();
                max = selgame.getPlayerLimit();

                String tmp ="";
                tmp += Integer.toString(current);
                tmp+="/";
                tmp+=Integer.toString(max);
                tv_lim.setText(tmp);
                tv_lim.setVisibility(View.VISIBLE);
                lim.setVisibility(View.VISIBLE);

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


                signedup = new String[selgame.getGoing().size()+1];
                images = new Bitmap[selgame.getGoing().size()+1];

                signedup[0] = "None";
                images[0] = null;

                for (int i =0; i < selgame.getGoing().size();i++)
                {
                    signedup[i+1] = selgame.getGoing().get(i);
                }



                Spinner spin = (Spinner) findViewById(R.id.signeup);
                spin.setOnItemSelectedListener(new ViewGame.playersListener());

                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), signedup);
                spin.setAdapter(customAdapter);

            }

        }

    }

    public void openMaps(View v) {
        Intent i = new Intent(ViewGame.this, MapsActivity.class);
        i.putExtra("callingActivity", 1);

        i.putExtra("location", coordinates);
        startActivity(i);
    }
}

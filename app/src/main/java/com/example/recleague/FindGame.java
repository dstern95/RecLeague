package com.example.recleague;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FindGame extends AppCompatActivity{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COURSE_LOCATION = 1;
    private final String TAG = "findgame";

    String[] gameArray = new String[1];
    ArrayList<gameProfile> masterlist;

    private String[] sports = {"All", "Soccer", "Basketball", "Water Polo"};
    private int[] images = {R.drawable.sports, R.drawable.soccer, R.drawable.basketball, R.drawable.water_polo};
    private String sport;
    private boolean load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        gameArray[0] = "Loading...";

        sport = "All";

        Spinner spin = (Spinner) findViewById(R.id.sports);
        spin.setOnItemSelectedListener(new FindGame.sportsListener());

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), images, sports);
        spin.setAdapter(customAdapter);
        load = false;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //GenericTypeIndicator<Date> t = new GenericTypeIndicator<Date>() {};
                //Date tmp = dataSnapshot.getValue(t);
                //gameHolder tmp = dataSnapshot.getValue(gameHolder.class);

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {
                };
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);
                final ListView listView = (ListView) findViewById(R.id.game_view);

                if (tmp != null) {
                    load = true;
                    gameHolder tmp2 = new gameHolder(tmp);
                    tmp = (ArrayList<gameProfile>) tmp2;
                    //this is surprisingly the most efficient solution of taking out the most recent dates

                    listView.setVisibility(View.VISIBLE);
                    gameArray = new String[tmp.size()];
                    masterlist = tmp;
                    //Log.d(TAG, Integer.toString(tmp.size()));

                    for (int i = 0; i < tmp.size(); i++) {
                        String name = tmp.get(i).getLocation();
                        name += "    ";
                        name += tmp.get(i).getDateTime().toString();
                        gameArray[i] = name;
                    }
                    update();
                } else {
                    listView.setVisibility(View.INVISIBLE);
                }


                //gameArray = tmp.toArray(new String[tmp.size()]);
                //GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                //List<String> value = dataSnapshot.getValue(t);
                //gameArray = value.toArray(new String[value.size()]);

                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        update();

    }

    class sportsListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            sport = sports[pos];
            update();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Do nothing
        }
    }

    public void update()
    {
        ArrayList<gameProfile> ml = masterlist;
        String[] gameArray2 = gameArray;
        if(!sport.equals("All"))
        {
            ml = new ArrayList<gameProfile>();
            for (int i =0; i<masterlist.size();i++)
            {
                if (masterlist.get(i).getSport().equals(sport))
                {
                    Log.d(TAG,masterlist.get(i).getSport());
                    ml.add(masterlist.get(i));
                }
            }
            gameArray2 = new String[ml.size()];
            for (int i = 0;i<ml.size();i++)
            {
                String name = ml.get(i).getLocation();
                name += "    ";
                name += ml.get(i).getDateTime().toString();
                gameArray2[i] = name;
            }
        }
        final ArrayList<gameProfile> ml2 = ml;
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gameArray2);

        final ListView listView = (ListView) findViewById(R.id.game_view);
        listView.setAdapter(itemsAdapter);
        listView.setClickable(load);
        listView.setEnabled(load);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String filename = listView.getItemAtPosition(position).toString();
                if (load) {

                    Intent i = new Intent(FindGame.this, ViewGame.class);
                    i.putExtra("GameName", ml2.get(position).getId());


                    startActivity(i);
                }

            }
        });

    }

    public void postGame(View v) {
            Intent i = new Intent(FindGame.this, PostGame.class);
            startActivity(i);

    }

}

package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FindGame extends AppCompatActivity{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COURSE_LOCATION = 1;
    private final String TAG = "findgame";

    String[] gameArray = new String[1];
    ArrayList<gameProfile> masterlist;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    private String[] sports = {"All", "Soccer", "Basketball", "Water Polo"};
    private int[] images = {R.drawable.sports, R.drawable.soccer, R.drawable.basketball, R.drawable.water_polo};
    private String sport;
    private boolean load;
    private ArrayList<gameProfile> ml3;
    double fardist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        fardist = -1;
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

                //Log.d(TAG,"current "+curLatlng.toString());
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {
                };
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);
                ml3 = tmp;
                fardist=5;
                changedbasesettings();


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

        SeekBar distance = (SeekBar) findViewById(R.id.distance);

        /*distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

        });*/

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

    public void changedbasesettings()
    {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String lat = sharedpreferences.getString("latitude", "0");
        String longitude = sharedpreferences.getString("longitude", "0");

        LatLng curLatlng = new LatLng(Double.valueOf(lat), Double.valueOf(longitude));
        ArrayList<gameProfile> tmp = ml3;
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
                if (tmp.get(i).getLatLlng() != null) {
                    double d = distancecalc(tmp.get(i).getLatLlng(), curLatlng);
                    name += "  " + Double.toString(d) + "Mi away  in ";
                }
                else
                {
                    name += "   ";
                }
                name += tmp.get(i).getDateTime().toString();
                gameArray[i] = name;
            }
            update();
        } else {
            listView.setVisibility(View.INVISIBLE);
        }



    }

    public void update()
    {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String lat = sharedpreferences.getString("latitude", "0");
        String longitude = sharedpreferences.getString("longitude", "0");

        LatLng curLatlng = new LatLng(Double.valueOf(lat), Double.valueOf(longitude));
        ArrayList<gameProfile> ml = masterlist;
        String[] gameArray2 = gameArray;


        if(!sport.equals("All")||fardist>0)
        {
            Log.d(TAG, "here");
            ml = new ArrayList<gameProfile>();
            for (int i =0; i<masterlist.size();i++)
            {
                if (masterlist.get(i).getSport().equals(sport)||sport.equals("All"))
                {
                    double d;
                    if(masterlist.get(i).getLatLlng() != null) {
                        d = distancecalc(masterlist.get(i).getLatLlng(), curLatlng);
                    }
                    else {
                        d=238900;
                        //im assuming if someone doesnt name a coordinate then the game is being played on the moon
                    }
                    if (d <= fardist||fardist ==-1) {
                        Log.d(TAG, masterlist.get(i).getSport());
                        ml.add(masterlist.get(i));
                    }
                }
            }
            gameArray2 = new String[ml.size()];
            for (int i = 0;i<ml.size();i++)
            {
                String name = ml.get(i).getLocation();

                if (ml.get(i).getLatLlng() != null) {
                    double d = distancecalc(ml.get(i).getLatLlng(), curLatlng);
                    name += "  " + Double.toString(d) + "Mi away  in ";
                }
                else
                {
                    name += "   ";
                }


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

    public Double distancecalc(LatLng coord, LatLng cur)
    {

        double R = 6372.8;
        double lat1 = coord.latitude;
        double lon1 = coord.longitude;
        double lat2 = cur.latitude;
        double lon2 = cur.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double dist=R * c *0.62137119;
        BigDecimal bd = new BigDecimal(dist);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        dist = bd.doubleValue();
        return dist;






        /*
        Log.d(TAG, cur.toString());
        int earthradius = 6371;
        double newLat = Math.toRadians(coord.latitude-cur.latitude);
        double newLong = Math.toRadians(coord.longitude-cur.longitude);
        double tmp = Math.sin(newLat/2)* Math.sin(newLong/2) +
                Math.cos(Math.toRadians(coord.latitude)*Math.toRadians(cur.latitude)*Math.sin(newLong/2)*Math.sin(newLong/2));


        double c = 2*Math.atan2(Math.sqrt(tmp),Math.sqrt(1-tmp));
        Log.d(TAG, Double.toString(tmp));
        double distancekm = earthradius * c;
        distancekm = Math.pow(distancekm,2);
        distancekm = Math.sqrt(distancekm);
        Log.d(TAG, Double.toString(distancekm));
*/
        //return (0.62137119*distancekm);
    }

    public void postGame(View v) {
            Intent i = new Intent(FindGame.this, PostGame.class);
            startActivity(i);

    }

}

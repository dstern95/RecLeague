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

public class FindGame extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COURSE_LOCATION = 1;
    private final String TAG = "findgame";

    String[] gameArray = new String[1];
    ArrayList<gameProfile> masterlist;

    private String[] sports = {"All", "Soccer", "Basketball", "Water Polo"};
    private int[] images = {R.drawable.sports, R.drawable.soccer, R.drawable.basketball, R.drawable.water_polo};
    private String sport;
    private boolean load;

    private String mLatitudeText;
    private String mLongitudeText;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

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

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(FindGame.this)
                    .addOnConnectionFailedListener(FindGame.this)
                    .addApi(LocationServices.API)
                    .build();
        }

        update();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());

            Log.d(TAG, "Latitude: " + mLatitudeText);
            Log.d(TAG, "Longitude: " + mLongitudeText);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    createLocationRequest();

                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);

                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                                    builder.build());





                } else {

                }
                return;
            }


        }
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


}

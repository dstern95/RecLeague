package com.example.recleague;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String MyPREFERENCES = "MyPrefs" ;
    private final static int REQUEST_CODE = 001;
    public static final int RequestPermissionCode = 1;
    private static final String TAG = "Main Activity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COURSE_LOCATION = 1;


    SharedPreferences sharedpreferences;
    String user;
    userProfile curUser;

    private String mLatitudeText;
    private String mLongitudeText;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        user = sharedpreferences.getString("user",null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user2 = mAuth.getCurrentUser();

        if (user2 != null) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("user", user2.getDisplayName());

            editor.apply();
        }

        // Request the permission be turned on
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        if(!checkPerm()){
            requestPerm();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(user2.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<userProfile> t = new GenericTypeIndicator<userProfile>() {};
                userProfile tmp = dataSnapshot.getValue(t);

                if(tmp == null)
                {
                    newUser(user2);
                }
                else
                {
                    curUser = tmp;
                }


                TextView tv = (TextView) findViewById(R.id.tv_intro);

                String intrmessage = "Welcome to RecLeague, ";
                intrmessage += curUser.getNickname();
                intrmessage += "!";
                tv.setText(intrmessage);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



/*
        String intrmessage = "hello ";
        intrmessage += curUser.getUsername();
        intrmessage += " welcome to RecLeague";
        tv.setText(intrmessage);
*/
        gameHolder games = new gameHolder();
        Date dt = new Date(2018,3,3,3,3);
        Date dt2 = new Date(2018,3,3,2,1);
        LatLng a2 = new LatLng(0,0);
        gameProfile gm = new gameProfile("here","coding",5,"me",dt, "sdfjk",a2.toString());
        games.insadd(gm);

        gameProfile gm2 = new gameProfile("hell","idk",7,"me",dt2, "jadkd",a2.toString());
        games.insadd(gm2);

        ArrayList<gameProfile> a = new ArrayList<gameProfile>();
        //a.add(gm);
        //a.add(gm2);



        //FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        //DatabaseReference myRef3 = database2.getReference("game");
        //gameHolder games2 = new gameHolder(a);


        //myRef3.setValue(games);
        //user = user.replace(".", "@");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(MainActivity.this)
                    .addOnConnectionFailedListener(MainActivity.this)
                    .addApi(LocationServices.API)
                    .build();
        }



    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void requestPerm(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, RequestPermissionCode);
    }
    public boolean checkPerm(){
        int r1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        boolean c;
        if(r1 == PackageManager.PERMISSION_GRANTED){
            c = true;
        }
        else{
            c = false;
        }
        return c;
    }
    public void newUser(FirebaseUser user2)
    {
        curUser = new userProfile(user2.getDisplayName(),user2.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(user2.getUid());


        myRef.setValue(curUser);

    }
    public void post(View v)
    {

        Intent intent1 = new Intent(this, PostGame.class);
        startActivity(intent1);
    }
    public void find(View v)
    {
        Intent intent1 = new Intent(this, FindGame.class);
        startActivity(intent1);
    }
    public void usergames(View v)
    {
        Intent intent1 = new Intent(this, FindUserGame.class);
        startActivity(intent1);
    }
    public void userProfile(View v)
    {

        Intent i = new Intent(this, ViewUserProfile.class);
        i.putExtra("userId", curUser.getUserid());
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("fin",true);
        editor.apply();
        finish();
        //Disable back button
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

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("latitude", mLatitudeText);
            editor.putString("longitude", mLongitudeText);
            editor.apply();

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

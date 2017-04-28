package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "maps";

    private GoogleMap mMap;
    SharedPreferences sharedpreferences;

    int PLACE_PICKER_REQUEST = 1;

    public static final String MyPREFERENCES = "MyPrefs" ;
    int callingActivity;

    boolean mapMarkers;
    LatLng markerPos;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mapMarkers = false;
        markerPos = null;

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {

            finish();
        }

        else {
            callingActivity = bundle.getInt("callingActivity");
        }

        if (callingActivity == 1) {
            Button placeBtn = (Button) findViewById(R.id.open_places);
            placeBtn.setVisibility(View.INVISIBLE);
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (callingActivity == 0) {
            fromPostGame(googleMap);
        }
        else {
            fromViewGame(googleMap);
        }

    }

    @Override
    public void onBackPressed() {
        if (callingActivity == 0) {
            Intent i = new Intent();
            i.putExtra("location", markerPos);
            setResult(RESULT_OK, i);
        }

        super.onBackPressed();
    }


    public void fromPostGame(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        String lat = sharedpreferences.getString("latitude", "0");
        String longitude = sharedpreferences.getString("longitude", "0");

        LatLng curLocation = new LatLng(Double.valueOf(lat), Double.valueOf(longitude));
        markerPos = curLocation;



        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
        //for some reason it is buggy on some phones unless this is done twice




        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if (!mapMarkers) {
                    mMap.clear();
                }

                mMap.addMarker(new MarkerOptions().position(latLng));
                markerPos = latLng;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("loc", latLng.toString());
                editor.apply();


            }
        });

    }

    public void fromViewGame(GoogleMap googleMap) {
        Bundle bundle = getIntent().getExtras();
        LatLng location = (LatLng) bundle.get("location");

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        markerPos = location;
        Log.d(TAG, location.toString());

        Marker m =mMap.addMarker(new MarkerOptions().position(location));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        Log.d(TAG, location.toString());

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        //for some reason it is buggy on some phones unless this is done twice

        Log.d(TAG, location.toString());
        Log.d(TAG,mMap.getCameraPosition().toString());


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng location = place.getLatLng();


                if (!mapMarkers) {
                    mMap.clear();
                }


                Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(place.getName().toString()));
                markerPos = location;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("loc", location.toString());
                editor.putString("locname",marker.getTitle());
                editor.apply();


                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                Log.d(TAG, location.toString());

                mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                //for some reason it is buggy on some phones unless this is done twice



            }
        }
    }

    public void openPlaces(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}


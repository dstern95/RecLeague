package com.example.recleague;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class PostGame extends AppCompatActivity {
    private final static String TAG = PostGame.class.getName();

    private String[] sports = new String[3];
    private String location;
    private String sport;
    private int playerLimit;
    private int currentPlayers;
    private String owner;
    private Date dateTime;

    public int hr;
    public int min;
    public int yr;
    public int dy;
    public int mth;

    gameHolder games;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String user;

    userProfile curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_game);
        games = new gameHolder();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        user = sharedpreferences.getString("user",null);
        sports[0] = "soccer";
        sports[1] = "basketball";
        sports[2] = "water-polo";
        //user = user.replace(".", "~");



        //GenericTypeIndicator<List<String>> games = new GenericTypeIndicator<List<String>>() {};
        //List games  =  new List();

        /*
        ArrayList<gameProfile> games = new ArrayList<gameProfile>();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");

        myRef.setValue(games);

        */
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user2 = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef2 = database.getReference(user2.getUid());
        myRef2.addValueEventListener(new ValueEventListener() {
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


        DatabaseReference myRef = database.getReference("game");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {};
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);
                if (tmp!=null) {
                    games = new gameHolder(tmp);
                }
                else
                {
                    games = new gameHolder();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        final Spinner s = (Spinner)findViewById(R.id.sports);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sports);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        s.setAdapter(spinnerArrayAdapter);
        s.setOnItemSelectedListener(new sportsListener());



    }
    public void create(View v)
    {

        EditText month = (EditText)findViewById(R.id.month_text);
        //int month = Integer.valueOf(etmonth.getText().toString());
        EditText day = (EditText)findViewById(R.id.day_text);
        EditText hour = (EditText)findViewById(R.id.hour_text);
        EditText min = (EditText)findViewById(R.id.min_text);

        EditText loc = (EditText)findViewById(R.id.location_text);
        location = loc.getText().toString();


        dateTime = new Date(117,Integer.valueOf(month.getText().toString()),Integer.valueOf(day.getText().toString()),
                Integer.valueOf(hour.getText().toString()), Integer.valueOf(min.getText().toString()));



        EditText lim = (EditText)findViewById(R.id.lim_text);
        playerLimit = Integer.valueOf(lim.getText().toString());
        Log.d(TAG, "Look here "+ location);


        gameProfile tmp = new gameProfile(location,sport,playerLimit,user,dateTime, curUser.getUserid());

        //ArrayList<gameProfile> games = new ArrayList<gameProfile>();
        games.insadd(tmp);


        curUser.addGame(tmp.getId());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");




        DatabaseReference myRef2 = database.getReference(curUser.getUserid());

        myRef.setValue(games);
        myRef2.setValue(curUser);


        Toast.makeText(PostGame.this, "Game posted",
                Toast.LENGTH_SHORT).show();

    }

    class sportsListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            sport = sports[pos];

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Do nothing
        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

        //Log.d(TAG, newFragment.getHr());

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user


        }



    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

        }
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}

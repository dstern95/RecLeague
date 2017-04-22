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

    private String[] sports = {"Soccer", "Basketball", "Water Polo"};
    private int[] images = {R.drawable.soccer, R.drawable.basketball, R.drawable.water_polo};

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

    public boolean timeSet;
    public boolean dateSet;

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

        hr = -1;
        min = -1;
        yr = -1;
        dy = -1;
        mth = -1;

        timeSet = false;
        dateSet = false;


        user = sharedpreferences.getString("user",null);
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

        final Spinner s = (Spinner)findViewById(R.id.sports);

        /*ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sports);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        s.setAdapter(spinnerArrayAdapter);
        s.setOnItemSelectedListener(new sportsListener());*/

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.sports);
        spin.setOnItemSelectedListener(new sportsListener());

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), images, sports);
        spin.setAdapter(customAdapter);
    }

    public void create(View v)
    {
        boolean dateTimeSet = false;
        boolean playerLimitSet = false;

        if (timeSet && dateSet) {
            dateTime = new Date(yr-1900, mth, dy, hr, min);
            dateTimeSet = true;
        }

        EditText loc = (EditText)findViewById(R.id.location_text);
        location = loc.getText().toString();

        EditText lim = (EditText)findViewById(R.id.lim_text);
        String limitStr = lim.getText().toString();
        Log.d(TAG, "Look here "+ location);

        if (!limitStr.equals("")) {
            playerLimit = Integer.valueOf(limitStr);
            playerLimitSet = true;
        }

        if (!dateTimeSet && !playerLimitSet) {
            Toast.makeText(PostGame.this, "Please complete all fields",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!dateTimeSet) {
            Toast.makeText(PostGame.this, "Please choose a date and time",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!playerLimitSet) {
            Toast.makeText(PostGame.this, "Please choose a player limit",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            gameProfile tmp = new gameProfile(location, sport, playerLimit, user, dateTime, curUser.getUserid());

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

            finish();
        }

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

        PostGame activity;
        int hour;
        int minute;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            activity = (PostGame) getActivity();
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();

            if (activity.hr == -1) {
                hour = c.get(Calendar.HOUR_OF_DAY);
            }
            else {
                hour = activity.hr;
            }

            if (activity.min == -1) {
                minute = c.get(Calendar.MINUTE);
            }
            else {
                minute = activity.min;
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            activity.setHour(hourOfDay);
            activity.setMinute(minute);

            activity.timeSet = true;

            activity.updateTime();

        }



    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        PostGame activity;
        int year;
        int month;
        int day;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            activity = (PostGame) getActivity();

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            if (activity.yr == -1) {
                year = c.get(Calendar.YEAR);
            }
            else {
                year = activity.yr;
            }

            if (activity.mth == -1) {
                month = c.get(Calendar.MONTH);
            }
            else {
                month = activity.mth;
            }

            if (activity.dy == -1) {
                day = c.get(Calendar.DAY_OF_MONTH);
            }
            else {
                day = activity.dy;
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            activity.setYear(year);
            activity.setMonth(month);
            activity.setDay(day);

            activity.dateSet = true;

            activity.updateDate();

        }
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setHour(int hour) {
        hr = hour;
        Log.d(TAG, "hour: " + hour);
    }

    public void setMinute(int minute) {
        min = minute;
        Log.d(TAG, "minute: " + minute);
    }

    public void setYear(int year) {
        yr = year;
        Log.d(TAG, "year: " + year);
;    }

    public void setMonth(int month) {
        mth = month;
        Log.d(TAG, "month: " + month);
    }

    public void setDay(int day) {
        dy = day;
        Log.d(TAG, "day: " + day);
    }

    public void updateDate() {
        TextView tv_date = (TextView) findViewById(R.id.pdate);
        String date = mth + "/" + dy + "/" + yr;

        tv_date.setText(date);
    }

    public void updateTime() {
        TextView tv_time = (TextView) findViewById(R.id.ptime);
        String time;

        int newHr;
        String period;

        if (hr > 12) {
            newHr = hr - 12;
            period = "PM";
        }
        else {
            newHr = hr;
            period = "AM";
        }

        if (hr == 12) {
            period = "PM";
        }

        if (min < 10) {
            time = newHr + ":" + "0" + min + " " + period;
        }
        else {
            time = newHr + ":" + min + " " + period;
        }

        tv_time.setText(time);
    }


}

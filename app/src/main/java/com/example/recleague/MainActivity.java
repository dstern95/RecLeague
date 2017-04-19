package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String user;
    userProfile curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        user = sharedpreferences.getString("user",null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user2 = mAuth.getCurrentUser();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("user", user2.getEmail());
        editor.apply();


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

                String intrmessage = "hello ";
                intrmessage += curUser.getNickname();
                intrmessage += " welcome to RecLeague";
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
        Date dt = new Date(2017,3,3,3,3);
        Date dt2 = new Date(2016,3,3,2,1);
        gameProfile gm = new gameProfile("here","coding",5,"me",dt);
        games.insadd(gm);

        gameProfile gm2 = new gameProfile("hell","idk",7,"me",dt2);
        games.insadd(gm2);

        ArrayList<gameProfile> a = new ArrayList<gameProfile>();
        a.add(gm);
        a.add(gm2);



        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("game");
        gameHolder games2 = new gameHolder(a);


        //myRef.setValue(games);
        //user = user.replace(".", "@");
    }

    public void newUser(FirebaseUser user2)
    {
        curUser = new userProfile(user2.getEmail(),user2.getUid());

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

    @Override
    public void onBackPressed() {
        //Disable back button
    }
}

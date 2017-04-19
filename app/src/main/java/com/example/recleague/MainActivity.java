package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        user = sharedpreferences.getString("user",null);
        TextView tv = (TextView)findViewById(R.id.tv_intro);
        String intrmessage = "hello ";
        intrmessage += user;
        intrmessage += " welcome to RecLeague";
        tv.setText(intrmessage);

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("game");
        gameHolder games2 = new gameHolder(a);


        myRef.setValue(games);
        //user = user.replace(".", "@");
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
}

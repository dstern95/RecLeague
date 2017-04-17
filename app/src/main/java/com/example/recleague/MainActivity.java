package com.example.recleague;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

package com.example.recleague;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FindGame extends AppCompatActivity {

    String[] gameArray = new String[1];
    ArrayList<gameProfile> masterlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        gameArray[0] = "Loading...";


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
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {};
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);

                gameArray = new String[tmp.size()];
                masterlist = tmp;
                for (int i =0; i <tmp.size();i++)
                {
                    String name = tmp.get(i).getLocation();
                    name +="    ";
                    name +=tmp.get(i).getDateTime().toString();
                    gameArray[i] = name;
                }
                update();

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

    public void update()
    {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gameArray);

        final ListView listView = (ListView) findViewById(R.id.game_view);
        listView.setAdapter(itemsAdapter);
    }

}

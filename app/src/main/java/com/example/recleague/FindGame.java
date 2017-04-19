package com.example.recleague;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
    private String[] sports = new String[4];
    private String sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        gameArray[0] = "Loading...";
        sports[0] = "all";
        sports[1] = "soccer";
        sports[2] = "basketball";
        sports[3] = "water-polo";

        sport = "all";

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
    @Override
    protected void onResume(){
        super.onResume();
        final Spinner s = (Spinner)findViewById(R.id.sports);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sports);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        s.setAdapter(spinnerArrayAdapter);
        s.setOnItemSelectedListener(new sportsListener());



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
        if(!sport.equals("all"))
        {
            ml = new ArrayList<gameProfile>();
            for (int i =0; i<masterlist.size();i++)
            {
                if (masterlist.get(i).getSport().equals(sport))
                {
                    ml.add(masterlist.get(i));
                }
            }
            gameArray2 = new String[ml.size()];
            for (int i = 0;i<ml.size();i++)
            {
                String name = ml.get(i).getLocation();
                name +="    ";
                name +=ml.get(i).getDateTime().toString();
                gameArray2[i] = name;
            }
        }
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gameArray2);

        final ListView listView = (ListView) findViewById(R.id.game_view);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String filename = listView.getItemAtPosition(position).toString();

                Intent i = new Intent(FindGame.this, ViewGame.class);
                i.putExtra("GameName", masterlist.get(position).getId());


                startActivity(i);


            }
        });
    }

}

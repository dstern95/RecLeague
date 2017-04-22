package com.example.recleague;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserGame extends AppCompatActivity {

    userProfile curUser;
    ArrayList<gameProfile> masterlist;
    public String[] gameArray;
    public boolean load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user_game);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user2 = mAuth.getCurrentUser();
        load = false;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(user2.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
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


        DatabaseReference myRef2 = database.getReference("game");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<gameProfile>> t = new GenericTypeIndicator<ArrayList<gameProfile>>() {};
                ArrayList<gameProfile> tmp = dataSnapshot.getValue(t);

                if (tmp!= null) {
                    gameHolder tmp2=new gameHolder(tmp);
                    tmp = (ArrayList<gameProfile>) tmp2;
                    //this is surprisingly the most efficient solution of taking out the most recent dates

                    gameArray = new String[tmp.size()];
                    masterlist = tmp;
                    load = true;
                    for (int i = 0; i < tmp.size(); i++) {
                        String name = tmp.get(i).getLocation();
                        name += "    ";
                        name += tmp.get(i).getDateTime().toString();
                        gameArray[i] = name;
                    }

                    update();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    public void update()
    {
        String sport = "all";
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
        ml = new ArrayList<gameProfile>();
        for (int i =0; i<masterlist.size();i++)
        {
            if (curUser.playingGame(masterlist.get(i).getId()))
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
                    Intent i = new Intent(FindUserGame.this, ViewGame.class);
                    i.putExtra("GameName", ml2.get(position).getId());


                    startActivity(i);
                }

            }
        });
    }

}

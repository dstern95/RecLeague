package com.example.recleague;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.name;

public class ViewUserProfile extends AppCompatActivity {

    private final String TAG = "viewgame";

    boolean isUser;
    String viewid;
    userProfile curProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        Intent i = new Intent();
        Bundle extra = getIntent().getExtras();
        viewid = getIntent().getStringExtra("userId");
        isUser=false;
        Log.d(TAG, viewid);
        //EditText evn =(EditText)findViewById(R.id.ev_nickname);
        //evn.setText("change");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference vdata = database.getReference(viewid);
        vdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<userProfile> t = new GenericTypeIndicator<userProfile>() {};
                userProfile tmp = dataSnapshot.getValue(t);
                curProfile = tmp;
                update(tmp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void update(userProfile vuser)
    {

        EditText evn =(EditText)findViewById(R.id.ev_nickname);
        evn.setText(vuser.getNickname());
        Log.d(TAG, vuser.getNickname());
        EditText evu =(EditText)findViewById(R.id.ev_username);
        evu.setText(vuser.getUsername());
        EditText evr =(EditText)findViewById(R.id.ev_rating);
        evr.setText(Double.toString(vuser.getRating()));
        EditText evb =(EditText)findViewById(R.id.ev_bio);
        evb.setText(vuser.getBio());
        Button bn = (Button)findViewById(R.id.save_button);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user2 = mAuth.getCurrentUser();

        if (vuser.getUserid().equals(user2.getUid()))
        {
            evn.setClickable(true);
            evn.setFocusable(true);
            evn.setFocusableInTouchMode(true);

            evb.setClickable(true);
            evb.setFocusable(true);
            evb.setFocusableInTouchMode(true);
            isUser = true;
            bn.setText("save changes");

            RatingBar rb = (RatingBar)findViewById(R.id.dialog_ratingbar);
            rb.setVisibility(View.INVISIBLE);

        }
        else
        {
            isUser = false;
            bn.setText("Rate");
            RatingBar rb = (RatingBar)findViewById(R.id.dialog_ratingbar);
            rb.setVisibility(View.VISIBLE);



        }



    }
    public void change(View v)
    {
        if (isUser)
        {
            EditText evn =(EditText)findViewById(R.id.ev_nickname);
            EditText evb =(EditText)findViewById(R.id.ev_bio);

            curProfile.setBio(evb.getText().toString());
            curProfile.setNickname(evn.getText().toString());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(viewid);
            myRef.setValue(curProfile);
            Toast.makeText(ViewUserProfile.this, "Game posted",
                    Toast.LENGTH_SHORT).show();
        }
        else {


            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            final FirebaseUser user2 = mAuth.getCurrentUser();
            RatingBar rb = (RatingBar)findViewById(R.id.dialog_ratingbar);
            curProfile.addRating(user2.getUid(),(double)rb.getRating());

            Log.d(TAG, Integer.toString(curProfile.getRaters().size()));
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(viewid);
            myRef.setValue(curProfile);
            Toast.makeText(ViewUserProfile.this, "Rated",
                    Toast.LENGTH_SHORT).show();


        }


    }

}

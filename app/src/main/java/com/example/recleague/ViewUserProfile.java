package com.example.recleague;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import org.w3c.dom.Text;

import static android.R.attr.name;

public class ViewUserProfile extends AppCompatActivity {

    private final String TAG = "viewgame";

    boolean isUser;
    String viewid;
    userProfile curProfile;

    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        Intent i = new Intent();
        Bundle extra = getIntent().getExtras();
        viewid = getIntent().getStringExtra("userId");
        isUser = false;

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

        /*TextView tv = (TextView)findViewById(R.id.nickname_output);
        tv.setText(vuser.getNickname());

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



        }*/



    }
    /*public void change(View v)
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


    }*/

    public void openAlert(View v) {
        // get prompts.xml view
        LinearLayout rl = (LinearLayout) findViewById(R.id.user_profile_layout);
        LayoutInflater layoutInflater = LayoutInflater.from(ViewUserProfile.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, rl);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewUserProfile.this);
        alertDialogBuilder.setView(promptView);

        TextView title = (TextView)findViewById(R.id.alert_tv);

        final EditText editText = (EditText) promptView.findViewById(R.id.alert_ev);

        switch (v.getId()) {
            case R.id.tv_nickname:
                resultText = (TextView) findViewById(R.id.tv_nickname_result);
                editText.setHint("Nickname");
                title.setText(R.string.nickname);

            case R.id.tv_email:
                resultText = (TextView) findViewById(R.id.tv_email_result);
                editText.setHint("Email");
                title.setText(R.string.email);

            case R.id.tv_rating:
                resultText = (TextView) findViewById(R.id.tv_rating);
                editText.setHint("Rating");
                title.setText(R.string.rating);

        }




        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultText.setText(editText.getText());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}

package com.example.recleague;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;

import static android.R.attr.name;

public class ViewUserProfile extends AppCompatActivity {

    private final String TAG = "viewgame";
    private int PICK_IMAGE_REQUEST = 1;

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

        final ImageView photo = (ImageView) findViewById(R.id.profile_photo);
        photo.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "here");
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewUserProfile.this);
                builder.setMessage("Are you sure you want to remove the image?")
                        .setTitle("Remove Profile Picture");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        photo.setImageResource(R.drawable.profile_head);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do Nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
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

        final TextView title = (TextView)promptView.findViewById(R.id.alert_tv);

        final EditText editText = (EditText) promptView.findViewById(R.id.alert_ev);

        switch (v.getId()) {
            case R.id.tv_nickname:
                resultText = (TextView) findViewById(R.id.tv_nickname_result);
                editText.setHint("Nickname");
                title.setText(R.string.nickname);
                break;

            case R.id.tv_bio:
                resultText = (TextView) findViewById(R.id.tv_bio_result);
                editText.setHint("Bio");
                title.setText(R.string.bio);
                break;

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

    public void changePhoto(View v) {
        Intent intent = new Intent();
        //Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView photo = (ImageView) findViewById(R.id.profile_photo);
                photo.getWidth();
                photo.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

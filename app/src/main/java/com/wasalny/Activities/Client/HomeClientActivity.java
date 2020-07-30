package com.wasalny.Activities.Client;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wasalny.Activities.AuthActivity;
import com.wasalny.Activities.RoomsActivity;
import com.wasalny.Fonts.Comforta;
import com.wasalny.Model.Client;
import com.wasalny.Model.Driver;
import com.wasalny.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeClientActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mRef;

    Client currentClient, thisClient;
    Driver currentDriver;
    String userType;

    // Views
    CircleImageView profileImage;
    Comforta userName;
    Button btnAddJourney;
    Button btnProfile;
    Button btnPreviousJournies;
    Button btnAbout;
    ImageView logoutClient;
    ImageView seeMsgsClient;

    Intent intent;

    private static final String TAG = "HomeClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        mAuth = FirebaseAuth.getInstance();
        userType = getIntent().getStringExtra("type");



        if (userType!=null){
            if (userType.equals("client")){
                currentClient = (Client) getIntent().getSerializableExtra("user");
                saveUserInSharedPreferences(userType,currentClient);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.d(TAG, "onStart: Firebase User Null");
            startActivity(new Intent(HomeClientActivity.this, AuthActivity.class));
            finish();
        }else {
            initClientViews();
            handleClientViews((Client) getUserFromSharedPreferences());
            mRef = FirebaseDatabase.getInstance().getReference().child("Clients").child(mAuth.getCurrentUser().getUid());
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    thisClient= snapshot.getValue(Client.class);
                    Log.d(TAG, "onDataChange: image " + thisClient.getImage());
                    userName.setText(thisClient.getUser().getName());
                    Picasso.get().load(thisClient.getImage()).placeholder(R.drawable.placeholder).into(profileImage);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void initClientViews(){
        btnAddJourney = findViewById(R.id.btnAddJourney);
        btnProfile = findViewById(R.id.btnProfile);
        btnPreviousJournies = findViewById(R.id.btnPreviousJournies);
        btnAbout = findViewById(R.id.btnAbout);
        userName = findViewById(R.id.userName);
        logoutClient = findViewById(R.id.logoutClient);
        seeMsgsClient = findViewById(R.id.seeMsgsClient);
        profileImage = findViewById(R.id.profileImage);
    }

    private void handleClientViews(Client client) {

        //userName.setText(client.getUser().getName());

        logoutClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mAuth.signOut();
                emptySharedPref();
                startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                finish();
            }
        });

        btnAddJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddJourneyActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClientProfileActivity.class));
            }
        });

        seeMsgsClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), RoomsActivity.class);
                intent.putExtra("user","client");
                startActivity(intent);
            }
        });

        btnPreviousJournies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PreviousJourneyActivity.class));
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutUs();
            }
        });
    }

    private void saveUserInSharedPreferences(String type, Object user){
        Log.d(TAG, "saveUserInSharedPreferences: ");
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.putString("type", type);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    private Object getUserFromSharedPreferences(){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String type = mPrefs.getString("type","not");
        String json = mPrefs.getString("user", "");
        Log.d(TAG, "getUserFromSharedPreferences: "+type);
        if (type.equals("client")){
            return gson.fromJson(json, Client.class);
        }else {
            return gson.fromJson(json, Driver.class);
        }
    }

    private void emptySharedPref(){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("type","not");
        prefsEditor.commit();
        prefsEditor.apply();
    }

    private void showAboutUs(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        final View view = li.inflate(R.layout.abt_us_layout, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HomeClientActivity.this);

        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        Button btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
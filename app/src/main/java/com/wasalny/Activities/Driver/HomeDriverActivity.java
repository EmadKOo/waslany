package com.wasalny.Activities.Driver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.wasalny.Activities.AuthActivity;
import com.wasalny.Activities.RoomsActivity;
import com.wasalny.Adapters.ViewPagerAdapter;
import com.wasalny.Model.Client;
import com.wasalny.Model.Driver;
import com.wasalny.R;

public class HomeDriverActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Client currentClient;
    Driver currentDriver;
    String userType;

    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView logoutDriver;
    ImageView seeRooms;

    Intent intent;

    private static final String TAG = "HomeDriverActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);

        mAuth = FirebaseAuth.getInstance();
        userType = getIntent().getStringExtra("type");

        if (userType!=null){
            if (userType.equals("driver")){
                currentDriver = (Driver) getIntent().getSerializableExtra("user");
                saveUserInSharedPreferences(userType,currentDriver);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(HomeDriverActivity.this, AuthActivity.class));
            finish();
        }else {
            initViews();
            handleDriverViews((Driver) getUserFromSharedPreferences());
        }
    }

    private void initViews(){
        viewPager  =findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        logoutDriver = findViewById(R.id.logoutDriver);
        seeRooms = findViewById(R.id.seeRooms);
    }

    private void saveUserInSharedPreferences(String type, Object user){
        Log.d(TAG, "saveUserInSharedPreferences: ");
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.putString("type", type);
        prefsEditor.commit();
        prefsEditor.apply();
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

    private void handleDriverViews(Driver driver){
        logoutDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                emptySharedPref();
                startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                finish();
            }
        });

        seeRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), RoomsActivity.class);
                intent.putExtra("user","driver");
                startActivity(intent);
            }
        });
    }
}
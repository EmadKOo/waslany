package com.wasalny.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasalny.Activities.Client.HomeClientActivity;
import com.wasalny.Activities.Driver.HomeDriverActivity;
import com.wasalny.R;
import com.wasalny.Tools.CheckInternet;

public class MainActivity extends AppCompatActivity {

    CheckInternet checkInternet;
    boolean isConnected;
    DatabaseReference myRef;
    String msg;
    Intent intent;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternet = new CheckInternet(this);
        isConnected = checkInternet.checkConnection();

        if (isConnected) {
            myRef = FirebaseDatabase.getInstance().getReference().child("security").child("msg");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    msg = dataSnapshot.getValue().toString();
                    if (msg.equals("Security")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    if (getUserFromSharedPreferences().equals("client"))
                                        startActivity(new Intent(getApplicationContext(), HomeClientActivity.class));
                                    else if (getUserFromSharedPreferences().equals("driver"))
                                        startActivity(new Intent(getApplicationContext(), HomeDriverActivity.class));
                                    else if (getUserFromSharedPreferences().equals("not"))
                                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                                    else {
                                        Log.d(TAG, "run: not found");
                                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                                    }
//                    startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else {
                        intent = new Intent(getApplicationContext(), SecurityActivity.class);
                        intent.putExtra("msg", msg);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private String getUserFromSharedPreferences(){
        SharedPreferences mPrefs = getSharedPreferences("type",MODE_PRIVATE);
        Log.d("TAG ", "getUserFromSharedPreferences: " + mPrefs.getString("type","not"));
        return mPrefs.getString("type","nott");
    }
}
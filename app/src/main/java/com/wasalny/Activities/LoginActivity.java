package com.wasalny.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasalny.Activities.Client.HomeClientActivity;
import com.wasalny.Activities.Driver.HomeDriverActivity;
import com.wasalny.Model.Client;
import com.wasalny.Model.Driver;
import com.wasalny.R;

public class LoginActivity extends AppCompatActivity {

    EditText mailLogin, passwordLogin;
    TextView tvForgotPass, tvSignIn;
    Spinner spinnerTypeLogin;
    ProgressBar loginProgress;

    Driver foundDriver = null;
    Client foundClient = null;
    Intent intent;

    String[] spinnerTypeArray;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initViews();
        handleViews();
    }

    private void initViews(){
        mailLogin = findViewById(R.id.mailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvSignIn = findViewById(R.id.tvSignIn);
        loginProgress = findViewById(R.id.loginProgress);
        spinnerTypeLogin = findViewById(R.id.spinnerTypeLogin);
        spinnerTypeArray = getResources().getStringArray(R.array.userType);
        ArrayAdapter<String> spinneruserAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerTypeArray);
        spinneruserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeLogin.setAdapter(spinneruserAdapter);
    }

    private void handleViews(){
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });
    }

    public void SignIn(View view) {
        loginProgress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(mailLogin.getText().toString(), passwordLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loginProgress.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (spinnerTypeLogin.getSelectedItem().toString().equals("Driver")){
                        configureUserType("driver");
//                        Log.d(TAG, "onComplete: "+ getUserFromSharedPreferences());
                        getDriver(user.getUid());
                    }else {
                        configureUserType("client");
                        getClient(user.getUid());
                       // Log.d(TAG, "onComplete: "+ getUserFromSharedPreferences());
                    }
                }else {
                    loginProgress.setVisibility(View.GONE);
                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDriver( final String uid){
        mDatabase.child("Drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(uid)){
                        foundDriver = snapshot.getValue(Driver.class);
                        Log.d(TAG, "onDataChange: Found " + foundDriver.getUser().getName());
                        intent = new Intent(LoginActivity.this, HomeDriverActivity.class);
                        intent.putExtra("type", "driver");
                        intent.putExtra("user", foundDriver);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getClient( final String uid){
        mDatabase.child("Clients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(uid)){
                        foundClient = snapshot.getValue(Client.class);
                        intent = new Intent(LoginActivity.this, HomeClientActivity.class);
                        intent.putExtra("type", "client");
                        intent.putExtra("user", foundClient);
                        startActivity(intent);
                        finish();
                        Log.d(TAG, "onDataChange: Found " + foundClient.getUser().getName());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void SignUp(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    private void configureUserType(String type){
        SharedPreferences mPrefs = getSharedPreferences("type",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("type",type);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    //
//    private String getUserFromSharedPreferences(){
//        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
//        Log.d(TAG, "getUserFromSharedPreferences: login activity " + mPrefs.getString("type","not"));
//        return mPrefs.getString("type","not");
//    }
}
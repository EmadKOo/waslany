package com.wasalny.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.wasalny.Activities.Client.ClientRegister;
import com.wasalny.Activities.Driver.DriverRegister;
import com.wasalny.Model.User;
import com.wasalny.R;

public class RegisterActivity extends AppCompatActivity {

    EditText nameRegister, mailReg, passwordReg, rePasswordReg, phoneReg;
    Spinner spinnerGender, spinnerUserType;
    ImageView registerBack;
    String[] spinnerGenderArray, spinnerUserTypeArray;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();
        handleViews();
    }

    private void initViews(){
        nameRegister = findViewById(R.id.nameRegister);
        mailReg = findViewById(R.id.mailReg);
        passwordReg = findViewById(R.id.passwordReg);
        rePasswordReg = findViewById(R.id.rePasswordReg);
        phoneReg = findViewById(R.id.phoneReg);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        registerBack = findViewById(R.id.registerBack);
    }


    private void handleViews(){
        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this , AuthActivity.class));
                finish();
            }
        });

        spinnerGenderArray = getResources().getStringArray(R.array.gender);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerGenderArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(spinnerArrayAdapter);

        spinnerUserTypeArray = getResources().getStringArray(R.array.userType);
        ArrayAdapter<String> spinneruserAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerUserTypeArray);
        spinneruserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(spinneruserAdapter);

    }
    public void register(View view) {

        if (notEmpty().equals("Done")){
            if (spinnerUserType.getSelectedItem().toString().equals("Driver")){
                intent = new Intent(getApplicationContext(), DriverRegister.class);
            }else if (spinnerUserType.getSelectedItem().toString().equals("Client")) {
                intent = new Intent(getApplicationContext(), ClientRegister.class);
            }else {
                spinnerUserType.setSelection(0);
                intent = new Intent(getApplicationContext(), DriverRegister.class);
            }

            User user = new User(nameRegister.getText().toString(),mailReg.getText().toString(), passwordReg.getText().toString(), phoneReg.getText().toString(), spinnerGender.getSelectedItem().toString(), spinnerUserType.getSelectedItem().toString());
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }else {
            Snackbar.make(findViewById(android.R.id.content),notEmpty(),Snackbar.LENGTH_LONG).show();
        }
    }


    private String notEmpty(){
        if (nameRegister.getText().toString().trim().equals("")||mailReg.getText().toString().trim().equals("")||passwordReg.getText().toString().trim().equals("")||rePasswordReg.getText().toString().trim().equals("")||phoneReg.getText().toString().trim().equals("")){
            return "Please Fill all Fields";
        }else if (!passwordReg.getText().toString().equals(rePasswordReg.getText().toString())){
            return "Password not match";
        }else
            return "Done";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}
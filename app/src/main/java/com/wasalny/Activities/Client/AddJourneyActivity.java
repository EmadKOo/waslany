package com.wasalny.Activities.Client;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wasalny.Model.Journey;
import com.wasalny.R;

public class AddJourneyActivity extends AppCompatActivity {

    EditText fromET, toET, descriptionET, numOfPersons, anyConditions, expectedBudget;
    RadioButton immediately ,later, airCondition, notAdapted;
    CheckBox returnOrNot;
    Button btnAddJourney;

    Journey journey;

    String journeyTime, returnOrNo, carType;

    FirebaseAuth mAuth;
    DatabaseReference mReference;

    private static final String TAG = "AddJourneyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        initViews();
        addJourney();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initViews(){
        fromET = findViewById(R.id.fromET);
        toET = findViewById(R.id.toET);
        descriptionET = findViewById(R.id.descriptionET);
        numOfPersons = findViewById(R.id.numOfPersons);
        anyConditions = findViewById(R.id.anyConditions);
        expectedBudget = findViewById(R.id.expectedBudget);
        immediately = findViewById(R.id.immediately);
        later = findViewById(R.id.later);
        airCondition = findViewById(R.id.airCondition);
        notAdapted = findViewById(R.id.notAdapted);
        returnOrNot = findViewById(R.id.returnOrNot);
        btnAddJourney = findViewById(R.id.btnAddJourney);
    }


    private void addJourney(){

        btnAddJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (immediately.isChecked())
                    journeyTime = "Immediately";
                else
                    journeyTime = "Later";

                if (returnOrNot.isChecked())
                    returnOrNo = "yes";
                else
                    returnOrNo = "no";

                if (airCondition.isChecked())
                    carType = "Air Conditioner";
                else
                    carType = "Not adapted";

                journey = new Journey(
                        mAuth.getCurrentUser().getUid(),
                        "",
                        fromET.getText().toString(),
                        toET.getText().toString(),
                        descriptionET.getText().toString(),
                        numOfPersons.getText().toString(),
                        journeyTime,
                        returnOrNo,
                        carType,
                        anyConditions.getText().toString(),
                        expectedBudget.getText().toString(),
                        "0",
                        "still",
                        ""
                        );

                mReference = FirebaseDatabase.getInstance().getReference();
                journey.setJourneyID(mReference.push().getKey());
                mReference.child("Journeys").child(mAuth.getCurrentUser().getUid()).child(journey.getJourneyID()).setValue(journey).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onCompleted: journey Added");
                        }else {
                            Log.d(TAG, "onCompleted: failed");
                        }
                    }
                });
                finish();
            }
        });
    }
}

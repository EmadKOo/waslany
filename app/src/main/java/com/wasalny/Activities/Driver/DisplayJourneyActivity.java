package com.wasalny.Activities.Driver;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wasalny.Model.Chat;
import com.wasalny.Model.Journey;
import com.wasalny.Model.Room;
import com.wasalny.R;

import java.util.ArrayList;
import java.util.Date;

public class DisplayJourneyActivity extends AppCompatActivity {

    Journey journey;
    TextView displayDescription, fromDisplay, toDisplay, numberOfPassengersDisplay,
            journeyTimeDisplay, backOrNot, carTypeDisplay, moreConditionsDisplay,
            expectedBudgetDisplay,journeyStatus;

    Button sendPropsalBtn;
    Room thisRoom;
    ImageView backDisplayJ;

    FirebaseAuth mAuth;
    DatabaseReference mReference;

    private static final String TAG = "DisplayJourneyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_journey);

        journey = (Journey)getIntent().getSerializableExtra("journey");
        initViews();
        handleViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (journey.getClientID().equals(mAuth.getCurrentUser().getUid()))
            sendPropsalBtn.setVisibility(View.GONE);
        try{
            if (getIntent().getStringExtra("new").equals("old"))
                sendPropsalBtn.setVisibility(View.GONE);
        }catch (Exception e){
            Log.d(TAG, "onStart: "+ e.getMessage());
        }
    }

    private void initViews(){
        displayDescription = findViewById(R.id.displayDescription);
        fromDisplay = findViewById(R.id.fromDisplay);
        toDisplay = findViewById(R.id.toDisplay);
        numberOfPassengersDisplay = findViewById(R.id.numberOfPassengersDisplay);
        journeyTimeDisplay = findViewById(R.id.journeyTimeDisplay);
        backOrNot = findViewById(R.id.backOrNot);
        carTypeDisplay = findViewById(R.id.carTypeDisplay);
        moreConditionsDisplay = findViewById(R.id.moreConditionsDisplay);
        expectedBudgetDisplay = findViewById(R.id.expectedBudgetDisplay);
        journeyStatus = findViewById(R.id.journeyStatus);
        sendPropsalBtn = findViewById(R.id.sendPropsalBtn);
        backDisplayJ = findViewById(R.id.backDisplayJ);
    }

    private void handleViews(){
        displayDescription.setText(journey.getDescription());
        fromDisplay.setText("From : " +journey.getFrom());
        toDisplay.setText("To : "+journey.getTo());
        numberOfPassengersDisplay.setText("Number of Passengers : "+journey.getNumOfPassengers());
        journeyTimeDisplay.setText("Journey Time : "+journey.getJourneyTime());
        backOrNot.setText("Going and Coming back : "+journey.getBackOrNot());
        carTypeDisplay.setText("Car Type : "+journey.getCarType());
        moreConditionsDisplay.setText("More Conditions : "+journey.getMoreConditions());
        expectedBudgetDisplay.setText("Expected Budget : "+journey.getExpectedBudget());
        journeyStatus.setText("Journey Status : "+journey.getJourneyStatus());

        backDisplayJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendPropsalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View view = li.inflate(R.layout.send_proposal_layout, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        DisplayJourneyActivity.this);

                alertDialogBuilder.setView(view);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                final EditText overviewET = view.findViewById(R.id.proposalOverview);
                final EditText roomTitle = view.findViewById(R.id.roomTitle);
                Button submitBTN = view.findViewById(R.id.sendProposalOverview);

                submitBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add this overview in firebase database
                        /**
                         * create new room its title is journeyID
                         * and have clientID & DriverID
                         * and have another node inside it
                         * contains the chat between them
                         * and first msg is overview from driver
                         */
                        mReference = FirebaseDatabase.getInstance().getReference().child("rooms");
                        thisRoom = new Room();
                        thisRoom.setClientID(journey.getClientID());
                        thisRoom.setDriverID(mAuth.getCurrentUser().getUid());
                        thisRoom.setRoomTitle(roomTitle.getText().toString());
                        thisRoom.setRoomID(mReference.push().getKey());
                        thisRoom.setJourneyID(journey.getJourneyID());

                        sendMessage(overviewET.getText().toString());
                        ArrayList<Chat> chats = new ArrayList<Chat>();
                        chats.add(new Chat(overviewET.getText().toString(),0,new Date().toString()));

                        thisRoom.setChatArrayList(chats);

                        mReference.child(mAuth.getCurrentUser().getUid()).child(journey.getClientID()).child(thisRoom.getRoomID()).setValue(thisRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "onComplete: success");
                                }else {
                                    Log.d(TAG, "onComplete: failed");
                                }
                            }
                        });
                        alertDialog.dismiss();
                        finish();
                    }
                });

                alertDialog.show();
            }
        });
    }

    private void sendMessage(String fMsg){
        int flag;
        DatabaseReference mReferenceMSG = FirebaseDatabase.getInstance().getReference();
        /**
         * if current id == client id of current room => flag = 1
         */

        if (mAuth.getCurrentUser().getUid().equals(thisRoom.getClientID()))
            flag = 1;
        else
            flag = 0;

        Chat chat = new Chat(fMsg,flag, new Date().toString());

        mReferenceMSG.child("Chats")
                .child(thisRoom.getRoomID())
                .child(thisRoom.getClientID())
                .child(thisRoom.getDriverID()).push()
                .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG, "onComplete: success");
                else
                    Log.d(TAG, "onComplete: failed");
            }
        });
    }
}
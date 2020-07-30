package com.wasalny.Activities.Client;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasalny.Adapters.JourneysAdapter;
import com.wasalny.Model.Journey;
import com.wasalny.Model.Room;
import com.wasalny.R;

import java.util.ArrayList;

public class PreviousJourneyActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mJourneysRef;

    ImageView backPrevJ;

    ArrayList<Journey> allJourneysList = new ArrayList<>();
    Journey journey = new Journey();
    JourneysAdapter journeysAdapter;

    ArrayList<Journey> prevJourneysList = new ArrayList<>();
    RecyclerView previousRecyclerView;
    DatabaseReference mPreviousRoomsRef;
    ArrayList<Room> roomArrayList = new ArrayList<>();
    Room room = new Room();

    private static final String TAG = "PreviousJourneyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_journey);

        initRecyclerView();
        backPrevJ = findViewById(R.id.backPrevJ);
        backPrevJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mPreviousRoomsRef = FirebaseDatabase.getInstance().getReference().child("previous");
        mJourneysRef = FirebaseDatabase.getInstance().getReference().child("Journeys");
    }

    @Override
    protected void onResume() {
        super.onResume();
        prevJourneysList.clear();
        mPreviousRoomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot snap: snapshot.getChildren()) {
                        if (mAuth.getCurrentUser().getUid().equals(snap.getKey())){
                            for (DataSnapshot mSnap: snap.getChildren()) {
                                room = mSnap.getValue(Room.class);
                                roomArrayList.add(room);

                                Log.d(TAG, "onDataChange: getRoomID " + room.getRoomID());
                            }
                        }
                    }
                }

                mJourneysRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot journeySnapshot) {
                        allJourneysList.clear();

                        for (DataSnapshot snapshot: journeySnapshot.getChildren()) {
                            for (DataSnapshot sSnapshot: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: " + sSnapshot.getValue());
                                journey = sSnapshot.getValue(Journey.class);
                                allJourneysList.add(journey);
                            }
                        }

                        // get previous journeys here
                        Log.d(TAG, "onDataChange: sss " + allJourneysList.size());
                        for (int x = 0; x < roomArrayList.size(); x++) {
                            for (int y = 0; y < allJourneysList.size(); y++) {
                                Log.d(TAG, "onDataChange: getDriverID " + roomArrayList.get(x).getDriverID() );
                                Log.d(TAG, "onDataChange: getDriverID " + allJourneysList.get(y).getDriverID() );
                                //   if (roomArrayList.get(x).getDriverID().equals(allJourneysList.get(y).getDriverID())){

                                Log.d(TAG, "onDataChange: getClientID " + roomArrayList.get(x).getClientID());
                                Log.d(TAG, "onDataChange: getClientID " + allJourneysList.get(y).getClientID());
                                if (roomArrayList.get(x).getClientID().equals(allJourneysList.get(y).getClientID())){
                                    Log.d(TAG, "onDataChange: getJourneyID " + roomArrayList.get(x).getJourneyID());
                                    Log.d(TAG, "onDataChange: getJourneyID " + allJourneysList.get(y).getJourneyID());
                                    if (roomArrayList.get(x).getJourneyID().equals(allJourneysList.get(y).getJourneyID())){
                                        prevJourneysList.add(allJourneysList.get(y));
                                    }
                                }
                            }
                        }
                        journeysAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onDataChange: Filtered " + prevJourneysList.size());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initRecyclerView(){
        previousRecyclerView = findViewById(R.id.previousRecyclerView);
        previousRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        journeysAdapter = new JourneysAdapter(prevJourneysList,this,"old");
        previousRecyclerView.setAdapter(journeysAdapter);
    }
}
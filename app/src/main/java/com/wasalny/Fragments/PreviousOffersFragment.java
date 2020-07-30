package com.wasalny.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousOffersFragment extends Fragment {

    FirebaseAuth mAuth;
    
    DatabaseReference mJourneysRef;
    ArrayList<Journey> allJourneysList = new ArrayList<>();
    Journey journey = new Journey();
    JourneysAdapter journeysAdapter;

    ArrayList<Journey> prevJourneysList = new ArrayList<>();
    RecyclerView previousRecyclerView;
    DatabaseReference mPreviousRoomsRef;
    ArrayList<Room> roomArrayList = new ArrayList<>();
    Room room = new Room();

    private static final String TAG = "PreviousOffersFragment";
    public PreviousOffersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_previous_offers, container, false);
        initRecyclerView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mPreviousRoomsRef = FirebaseDatabase.getInstance().getReference().child("previous");
        mJourneysRef = FirebaseDatabase.getInstance().getReference().child("Journeys");
    }

    @Override
    public void onResume() {
        super.onResume();
        prevJourneysList.clear();
        mPreviousRoomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (mAuth.getCurrentUser().getUid().equals(snapshot.getKey())){
                        for (DataSnapshot snap: snapshot.getChildren()) {
                            for (DataSnapshot mSnap: snap.getChildren()) {
                                room = mSnap.getValue(Room.class);
                                roomArrayList.add(room);
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
                            //    }
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

    private void initRecyclerView(View view){
        previousRecyclerView = view.findViewById(R.id.previousRecyclerView);
        previousRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        journeysAdapter = new JourneysAdapter(prevJourneysList,getActivity(),"old");
        previousRecyclerView.setAdapter(journeysAdapter);
    }
}
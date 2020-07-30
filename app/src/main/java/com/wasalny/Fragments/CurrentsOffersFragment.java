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
import com.wasalny.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentsOffersFragment extends Fragment {


    RecyclerView journeysRecyclerView;
    ArrayList<Journey> journeysList = new ArrayList<>();
    Journey journey = new Journey();
    JourneysAdapter journeysAdapter;

    FirebaseAuth mAuth;
    DatabaseReference mReference;

    private static final String TAG = "CurrentsOffersFragment";
    public CurrentsOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_currents_offers, container, false);
        initRecyclerView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("Journeys");
    }


    @Override
    public void onResume() {
        super.onResume();

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                journeysList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot sSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: " + sSnapshot.getValue());
                        journey = sSnapshot.getValue(Journey.class);
                        journeysList.add(journey);
                        journeysAdapter.notifyDataSetChanged();
                    }
                }

                Log.d(TAG, "onDataChange: size " + journeysList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(View view){
        journeysRecyclerView = view.findViewById(R.id.journeysRecyclerView);
        journeysRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        journeysAdapter = new JourneysAdapter(journeysList,getActivity(),"new");
        journeysRecyclerView.setAdapter(journeysAdapter);
    }
}
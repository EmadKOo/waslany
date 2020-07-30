package com.wasalny.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasalny.Adapters.RoomsAdapter;
import com.wasalny.Model.Room;
import com.wasalny.R;

import java.util.ArrayList;

public class RoomsActivity extends AppCompatActivity {

    RecyclerView roomRecyclerView;
    ArrayList<Room> rooms = new ArrayList<>();
    RoomsAdapter roomsAdapter;

    FirebaseAuth mAuth;
    DatabaseReference mReference;

    Room room = new Room();

    String userType;
    private static final String TAG = "RoomsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        userType = getIntent().getStringExtra("user");
        initViews();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("rooms");

        if (userType.equals("client"))
            loadRoomsClientSide();
        else
            loadRoomsDriverSide();
    }

    private void initViews(){
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsAdapter = new RoomsAdapter(rooms,this);
        roomRecyclerView.setAdapter(roomsAdapter);
    }

    private void loadRoomsDriverSide(){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rooms.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (mAuth.getCurrentUser().getUid().equals(snapshot.getKey())){
                        Log.d(TAG, "onDataChange: VAlue  children count " + snapshot.getChildrenCount());
                        for (DataSnapshot snap: snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: VAlA " + snap.getValue());
                            for (DataSnapshot mSnap: snap.getChildren()) {
                                room = mSnap.getValue(Room.class);
                                rooms.add(room);
                                Log.d(TAG, "onDataChange: roR " + room.getChatArrayList().get(0).getMessage());
                            }
                        }
                    }
                }
                roomsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onDataChange: Sizze "  + rooms.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadRoomsClientSide(){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                rooms.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for (DataSnapshot snap: snapshot.getChildren()) {
                        if (mAuth.getCurrentUser().getUid().equals(snap.getKey())){
                            Log.d(TAG, "onDataChange: KEY " + snap.getKey());
                            Log.d(TAG, "onDataChange: BAKL " + snap.getValue());
                            for (DataSnapshot sSnap: snap.getChildren()) {
                                room = sSnap.getValue(Room.class);
                                rooms.add(room);
                            }
                        }
                    }
                }

                roomsAdapter.notifyDataSetChanged();
                Log.d(TAG, "onDataChange: Sizze "  + rooms.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
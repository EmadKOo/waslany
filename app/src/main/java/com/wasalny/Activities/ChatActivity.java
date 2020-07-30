package com.wasalny.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasalny.Adapters.ChatAdapter;
import com.wasalny.Model.Chat;
import com.wasalny.Model.Room;
import com.wasalny.R;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    RecyclerView chatRecyclerview;
    EditText messageET;
    ImageView sendMessage;
    TextView chatTitle;
    ImageView moreChat;
    Toolbar chatToolbar;

    ChatAdapter chatAdapter;
    ArrayList<Chat> chatList = new ArrayList<>();
    Chat chat = new Chat();
    Room currentRoom;

    FirebaseAuth mAuth;
    DatabaseReference mReference;

    private static final String TAG = "ChatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentRoom = (Room) getIntent().getSerializableExtra("room");
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        loadData();
    }


    private void initViews(){
        chatToolbar = findViewById(R.id.chatToolbar);
        this.setSupportActionBar(chatToolbar);
        chatRecyclerview = findViewById(R.id.chatRecyclerview);
        chatRecyclerview.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        chatAdapter = new ChatAdapter(ChatActivity.this, chatList);
        chatRecyclerview.setAdapter(chatAdapter);

        messageET = findViewById(R.id.messageET);
        sendMessage = findViewById(R.id.sendMessage);
       // moreChat = findViewById(R.id.moreChat);
        chatTitle = findViewById(R.id.chatTitle);
        chatTitle.setText(currentRoom.getRoomTitle());

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage(){
        int flag;
        mReference = FirebaseDatabase.getInstance().getReference();
        /**
         * if current id == client id of current room => flag = 1
         */

        if (mAuth.getCurrentUser().getUid().equals(currentRoom.getClientID()))
            flag = 1;
        else
            flag = 0;

        Chat chat = new Chat(messageET.getText().toString(),flag, new Date().toString());

        mReference.child("Chats")
                .child(currentRoom.getRoomID())
                .child(currentRoom.getClientID())
                .child(currentRoom.getDriverID()).push()
                .setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG, "onComplete: success");
                else
                    Log.d(TAG, "onComplete: failed");
            }
        });
        messageET.setText("");
    }

    private void loadData(){
        chatList.clear();
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("Chats")
                .child(currentRoom.getRoomID())
                .child(currentRoom.getClientID())
                .child(currentRoom.getDriverID())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                Log.d(TAG, "onDataChange: SNaP  " + dataSnapshot.getValue());
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    chat = snapshot.getValue(Chat.class);
                    chatList.add(chat);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser().getUid().equals(currentRoom.getClientID()))
            getMenuInflater().inflate(R.menu.client_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.hireMenu){
            Log.d(TAG, "onOptionsItemSelected:  Hire");
            /**
             * add journey in the driver list ( current journey fragment )
             * 
             * and flip hire to end journey then delete node from current journey 
             * and add it to the previous journeys for the driver
             * 
             * then ask user & driver for the rate 
             *
             **/
            
            if (item.getTitle().equals("Hire")){
                mReference = FirebaseDatabase.getInstance().getReference();
                mReference.child("currentJourneys")
                        .child(currentRoom.getDriverID())
                        .child(currentRoom.getClientID())
                        .child(currentRoom.getRoomID())
                        .setValue(currentRoom)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: success");
                        }else {
                            Log.d(TAG, "onComplete: failed");
                        }
                    }
                });


                item.setTitle("End Journey");
            }else {
                /**
                 * journey finished delete it from current
                 * and from offers and add it to previous
                 */
                mReference.child("currentJourneys")
                        .child(currentRoom.getDriverID())
                        .child(currentRoom.getClientID())
                        .child(currentRoom.getRoomID())
                        .removeValue();

                mReference.child("rooms")
                        .child(currentRoom.getDriverID())
                        .child(currentRoom.getClientID())
                        .child(currentRoom.getRoomID())
                        .removeValue();

                mReference.child("previous")
                        .child(currentRoom.getDriverID())
                        .child(currentRoom.getClientID())
                        .child(currentRoom.getRoomID())
                        .setValue(currentRoom)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "onComplete: success");
                                }else {
                                    Log.d(TAG, "onComplete: failed");
                                }
                            }
                        });

               Toast.makeText(ChatActivity.this, "Ask for rate", Toast.LENGTH_SHORT).show();
               item.setVisible(false);
            }
        }else if (item.getItemId() == R.id.journeyInfo){

        }
        return super.onOptionsItemSelected(item);
    }
}
package com.wasalny.Activities.Client;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wasalny.Model.Client;
import com.wasalny.Model.User;
import com.wasalny.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClientProfileActivity extends AppCompatActivity {

    CircleImageView profileClientImage;
    EditText clientName, mailClient, phoneClient, cityClient;
    Button saveProfileClient;
    ProgressBar profileImageProgress;

    Client currentClient;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageRef;
    UploadTask uploadTask;

    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int STORAGE_REQUEST_CODE = 400;
    String storagePermission[];

    private static final String TAG = "ClientProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        initViews();
        handleViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
     
        databaseReference.child("Clients").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentClient = dataSnapshot.getValue(Client.class);
                clientName.setText(currentClient.getUser().getName());
                mailClient.setText(currentClient.getUser().getEmail());
                phoneClient.setText(currentClient.getUser().getPhone());
                cityClient.setText(currentClient.getCity());
                Picasso.get().load(currentClient.getImage()).placeholder(R.drawable.placeholder).into(profileClientImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void initViews(){
        profileClientImage = findViewById(R.id.profileClientImage);
        profileImageProgress = findViewById(R.id.profileImageProgress);
        clientName = findViewById(R.id.clientName);
        mailClient = findViewById(R.id.mailClient);
        phoneClient = findViewById(R.id.phoneClient);
        cityClient = findViewById(R.id.cityClient);
        saveProfileClient = findViewById(R.id.saveProfileClient);
        mailClient.setEnabled(false);
    }

    private void handleViews(){

        saveProfileClient.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                User user = new User(clientName.getText().toString(), mailClient.getText().toString(), currentClient.getUser().getPassword(), phoneClient.getText().toString(), currentClient.getUser().getGender(), currentClient.getUser().getType());
                Client client = new Client(user, cityClient.getText().toString(),"https://firebasestorage.googleapis.com/v0/b/wasalny-bec26.appspot.com/o/placeholder.png?alt=media&token=77896ac0-f757-4071-b08a-c39fdc1c46f8");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Clients").child(mAuth.getCurrentUser().getUid());
                databaseReference.setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: success");
                        }else {
                            Log.d(TAG, "onComplete: failed");
                        }
                    }
                });
           }
       });


        profileClientImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickGallry();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickGallry();
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                Toast.makeText(this, "REQUEST", Toast.LENGTH_SHORT).show();
                profileClientImage.setImageURI(data.getData());
                uploadImage(data.getData());
                Log.d(TAG, "onActivityResult: " + data.getData());
            }
        }
    }

    private void uploadImage(Uri file){

        final StorageReference imgsRef = storageRef.child("images/"+mAuth.getCurrentUser().getUid() +".png");
        uploadTask = imgsRef.putFile(file);
        profileImageProgress.setVisibility(View.VISIBLE);
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                profileImageProgress.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImageProgress.setVisibility(View.GONE);

                Log.d(TAG, "onSuccess: imgsRef.getDownloadUrl() "+ imgsRef.getDownloadUrl());

            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imgsRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Picasso.get().load(downloadUri).placeholder(R.drawable.placeholder).into(profileClientImage);
                    Log.d(TAG, "onComplete: downloadUri " + downloadUri.toString());

                    // update link firbase database
                    databaseReference.child("Clients").child(mAuth.getCurrentUser().getUid()).child("image").setValue(downloadUri.toString());

                } else {
                    // Handle failures
                }
            }
        });
    }
    private void pickGallry() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
}

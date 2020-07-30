package com.wasalny.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wasalny.Model.Driver;
import com.wasalny.Model.User;
import com.wasalny.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ProgressBar profileImageProgress;
    CircleImageView profileDriverImage;
    EditText driverName, mailLogin, phoneDriver, cityDriver, licenceDriver, carModelNumber;
    Button saveProfileDriver;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageRef;
    UploadTask uploadTask;

    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int STORAGE_REQUEST_CODE = 400;
    String storagePermission[];

    Driver currentDriver = null;

    private static final String TAG = "ProfileFragment";
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        initViews(view);
        handleViews();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseReference.child("Drivers").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDriver = dataSnapshot.getValue(Driver.class);
                driverName.setText(currentDriver.getUser().getName());
                mailLogin.setText(currentDriver.getUser().getEmail());
                phoneDriver.setText(currentDriver.getUser().getPhone());
                cityDriver.setText(currentDriver.getCityDriver());
                licenceDriver.setText(currentDriver.getDriverLicence());
                carModelNumber.setText(currentDriver.getCarModelNumber());
                try{
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.placeholder).into(profileDriverImage);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "  + e.getMessage());
                }
               }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void initViews(View view){
        profileDriverImage = view.findViewById(R.id.profileDriverImage);
        profileImageProgress = view.findViewById(R.id.profileImageProgress);
        driverName = view.findViewById(R.id.driverName);
        mailLogin = view.findViewById(R.id.mailLogin);
        phoneDriver = view.findViewById(R.id.phoneDriver);
        cityDriver = view.findViewById(R.id.cityDriver);
        licenceDriver = view.findViewById(R.id.licenceDriver);
        carModelNumber = view.findViewById(R.id.carModelNumber);
        saveProfileDriver = view.findViewById(R.id.saveProfileDriver);
    }

    private void handleViews(){

        saveProfileDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(driverName.getText().toString(), mailLogin.getText().toString(), currentDriver.getUser().getPassword(), phoneDriver.getText().toString(), currentDriver.getUser().getGender(), "Driver");
                Driver newDriver = new Driver(user, cityDriver.getText().toString(), carModelNumber.getText().toString(), licenceDriver.getText().toString(), currentDriver.getUserIdentity(),"https://firebasestorage.googleapis.com/v0/b/wasalny-bec26.appspot.com/o/placeholder.png?alt=media&token=77896ac0-f757-4071-b08a-c39fdc1c46f8");
                databaseReference.child("Drivers").child(mAuth.getCurrentUser().getUid()).setValue(newDriver).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        profileDriverImage.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                Toast.makeText(getActivity(), "REQUEST", Toast.LENGTH_SHORT).show();
                profileDriverImage.setImageURI(data.getData());
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
                    Picasso.get().load(downloadUri).placeholder(R.drawable.placeholder).into(profileDriverImage);
                    Log.d(TAG, "onComplete: downloadUri " + downloadUri.toString());

                    // update link firbase database
                    databaseReference.child("Drivers").child(mAuth.getCurrentUser().getUid()).child("image").setValue(downloadUri.toString());

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
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
}
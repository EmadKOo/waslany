package com.wasalny.Activities.Client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wasalny.Model.Client;
import com.wasalny.Model.User;
import com.wasalny.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClientRegister extends AppCompatActivity {

    EditText cityClient;
    CheckBox agree;
    CircleImageView clientImage;
    User user;
    Button register;
    ProgressBar sProgress;

    Client client;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    int SELECT_IMAGE = 10025;
    StorageReference storageRef;

    private static final int STORAGE_REQUEST_CODE = 400;
    String storagePermission[];
    Bitmap bitmapRecognition;
    String imageNameRecognition, clientImageURI;

    private static final String TAG = "ClientRegister";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_register);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        user = (User) getIntent().getSerializableExtra("user");
        clientImage = findViewById(R.id.clientImage);
        clientImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sProgress = findViewById(R.id.sProgress);
        Registeration();
    }

    private void Registeration() {
        register = findViewById(R.id.register);
        cityClient = findViewById(R.id.cityClient);
        agree = findViewById(R.id.agree);

        agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                register.setEnabled(isChecked);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sProgress.setVisibility(View.VISIBLE);

                if (cityClient.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Type Your City", Snackbar.LENGTH_LONG).show();
                    sProgress.setVisibility(View.GONE);

                } else if (!agree.isChecked()) {
                    Snackbar.make(findViewById(android.R.id.content), "You must agree our terms of services.", Snackbar.LENGTH_LONG).show();
                    sProgress.setVisibility(View.GONE);

                } else {
                    client = new Client(user, cityClient.getText().toString(),clientImageURI);
                    // add client to Firebase

                    mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                            .addOnCompleteListener(ClientRegister.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "createUserWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        // add driver to firebase database
                                        mDatabase.child("Clients").child(user.getUid()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "onComplete: data added to database");
                                                } else
                                                    Log.d(TAG, "onComplete: failed to add to database");
                                            }
                                        });
                                        sProgress.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(), HomeClientActivity.class));
                                        finish();
                                    } else {
                                        sProgress.setVisibility(View.GONE);
                                        Log.d(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    private void selectImage(){
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SELECT_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri uri = data.getData();
        clientImage.setImageURI(uri);
        try {
            bitmapRecognition = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addDriverToFirebase();
    }

    private void addDriverToFirebase() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapRecognition.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        imageNameRecognition =timeStamp + ".jpg";

        storageRef = FirebaseStorage.getInstance().getReference().child("users");
        final UploadTask uploadTask = storageRef.child(imageNameRecognition).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "onFailure: " + exception.getMessage());
                Log.d(TAG, "onFailure: FAILED ************************************");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d(TAG, "onSuccess: SUCCESS *****************************");
                storageRef = FirebaseStorage.getInstance().getReference().child("users/").child(imageNameRecognition);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: --------------" + uri.toString());
                        clientImageURI = uri.toString();
                    }
                });
            }
        });
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    selectImage();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
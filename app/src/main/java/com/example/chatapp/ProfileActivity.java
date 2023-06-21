package com.example.chatapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imageAccountProfile;
    private TextInputEditText editTextUserProfile;
    private Button buttonUpdate;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storageDb;
    StorageReference storageRef;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private Uri imageUri;
    private boolean userImage = false;

    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageAccountProfile = findViewById(R.id.imageAccountProfile);
        editTextUserProfile = findViewById(R.id.editTextUserProfile);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageDb = FirebaseStorage.getInstance();
        storageRef = storageDb.getReference();

        getUserInfo();

        activityResultLauncherImpl();

        imageAccountProfile.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        buttonUpdate.setOnClickListener(view -> {
            updateUserInfo();

        });


    }

    public void getUserInfo(){
        reference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("userName").getValue().toString();
                image = snapshot.child("image").getValue().toString();

                editTextUserProfile.setText(userName);

                if(image.equals("null")){
                    imageAccountProfile.setImageResource(R.drawable.account);
                }
                else{
                    Picasso.get().load(image).into(imageAccountProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateUserInfo(){
        String userName = editTextUserProfile.getText().toString();
        reference.child("Users").child(user.getUid()).child("userName").setValue(userName);

        if(userImage == true){
            UUID randomId = UUID.randomUUID();
            String image = "image/"+randomId+".jpg";
            storageRef.child(image).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ProfileActivity.this, "Image added to store", Toast.LENGTH_LONG).show();
                    StorageReference referenceImage = storageDb.getReference(image);
                    referenceImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String dbImagePath = uri.toString();
                            reference.child("Users").child(auth.getUid()).child("image").setValue(dbImagePath).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "user image path added to database", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(ProfileActivity.this, "user image path failed to add to database", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
        else{
            reference.child("Users").child(auth.getUid()).child("image").setValue(image);
            Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
            startActivity(intent);

        }

    }

    public void activityResultLauncherImpl(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if(resultCode == RESULT_OK && data!=null){
                            imageUri = data.getData();
                            imageAccountProfile.setImageURI(imageUri);
                            userImage = true;
                        }
                    }
                });
    }
}
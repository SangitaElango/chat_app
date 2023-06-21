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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView imageViewAccount;
    private TextView textView;
    private TextInputEditText editTextEmailSignup, editTextPassSignup,editTextNameSignup;
    private Button buttonRegister;

    private ActivityResultLauncher<Intent> activityResultLauncherSelectImage;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storageDb;
    StorageReference storageRef;

    private boolean userImage = false;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageViewAccount = findViewById(R.id.imageAccountProfile);
        textView = findViewById(R.id.textView);
        editTextEmailSignup = findViewById(R.id.editTextEmailSignup);
        editTextPassSignup = findViewById(R.id.editTextPassSignup);
        editTextNameSignup = findViewById(R.id.editTextNameSignup);
        buttonRegister = findViewById(R.id.buttonRegister);

        activityResultLauncherSelectImageImpl();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storageDb = FirebaseStorage.getInstance();
        storageRef = storageDb.getReference();

        imageViewAccount.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncherSelectImage.launch(intent);

        });

        buttonRegister.setOnClickListener(view -> {

            signUp();

        });
    }

    public void activityResultLauncherSelectImageImpl(){
        activityResultLauncherSelectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultcode = result.getResultCode();
                        Intent data = result.getData();
                        if (resultcode == RESULT_OK && data != null) {
                            Log.i("ResultLauncherSelectImg","selecting image");
                            imageUri = data.getData();
                            Picasso.get().load(imageUri).into(imageViewAccount);
                            userImage = true;
                            Toast.makeText(SignUpActivity.this, "userImage true", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Log.i("ResultLauncherSelectImg","Problem in selecting image");
                        }
                    }
                });
    }

    public void signUp() {
        String email = editTextEmailSignup.getText().toString();
        String pass = editTextPassSignup.getText().toString();
        String userName = editTextNameSignup.getText().toString();
        if (!email.equals("") && !pass.equals("") && !userName.equals("")) {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                            reference.child("Users").child(auth.getUid()).child("userName").setValue(userName);
                            if (userImage == true) {
                                UUID randomId = UUID.randomUUID();
                                String image = "image/" + randomId + ".jpg";
                                storageRef.child(image).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(SignUpActivity.this, "Image added to store", Toast.LENGTH_LONG).show();
                                        StorageReference referenceImage = storageDb.getReference(image);
                                        referenceImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String dbImagePath = uri.toString();
                                                reference.child("Users").child(auth.getUid()).child("image").setValue(dbImagePath).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SignUpActivity.this, "user image path added to database", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(SignUpActivity.this, "user image path failed to add to database", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            } else {
                                reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);

                            }

                    } else {
                        Toast.makeText(SignUpActivity.this, R.string.null_value, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        else{
            Toast.makeText(SignUpActivity.this, R.string.null_value, Toast.LENGTH_LONG).show();
        }
    }
}
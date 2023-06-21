package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextInputEditText editTextEmail, editTextPass;
    private Button buttonSignIn, buttonSignUp;
    private TextView textViewForget;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        editTextEmail = findViewById(R.id.editTextEmailSignup);
        editTextPass = findViewById(R.id.editTextPassSignup);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewForget = findViewById(R.id.textViewForget);

        user = auth.getCurrentUser();
        /*if (user != null) {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }*/

        buttonSignIn.setOnClickListener(view -> {
            signIn();
        });

        buttonSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);

        });

        textViewForget.setOnClickListener(view -> {

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            Log.i("Login","user"+user.toString());
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void signIn(){
        String email = editTextEmail.getText().toString();
        String pass = editTextPass.getText().toString();
        if(!email.equals("") && !pass.equals("")) {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Toast.makeText(LoginActivity.this, R.string.toast_success, Toast.LENGTH_LONG);
                        startActivity(intent);
                    } else {
                        Log.e("Signup Error", "onCancelled", task.getException());
                        Toast.makeText(LoginActivity.this, R.string.null_value, Toast.LENGTH_LONG);
                    }
                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, R.string.null_value, Toast.LENGTH_LONG);
        }
    }
}
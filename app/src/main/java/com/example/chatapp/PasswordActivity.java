package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private TextInputEditText editTextEmailReset;
    private Button buttonReset;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        auth = FirebaseAuth.getInstance();

        editTextEmailReset = findViewById(R.id.editTextEmailReset);
        buttonReset = findViewById(R.id.buttonReset);

        buttonReset.setOnClickListener(view -> {
            String email = editTextEmailReset.getText().toString();
            if(!email.equals("")){
                passwordReset(email);
            }
            else{
                Toast.makeText(PasswordActivity.this,R.string.null_value,Toast.LENGTH_LONG).show();
            }

        });
    }

    public void passwordReset(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PasswordActivity.this,R.string.reset_email,Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(PasswordActivity.this,R.string.reset_email_fail,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
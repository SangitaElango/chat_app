package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private TextView textViewChat;
    private EditText editTextMsg;
    private FloatingActionButton buttonSend;
    private RecyclerView rvChat;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String otherName;
    String userName;
    MessageAdapter msgAdapter;
    List<Message> msgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewChat = findViewById(R.id.textViewChat);
        editTextMsg = findViewById(R.id.editTextMsg);
        buttonSend = findViewById(R.id.buttonSend);
        rvChat = findViewById(R.id.rv);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        Intent intent = getIntent();
        otherName = intent.getStringExtra("otherName");
        Log.i("ChatActivity",otherName);
        userName = intent.getStringExtra("userName");
        Log.i("ChatActivity",userName);


        textViewChat.setText(otherName);
        imageViewBack.setOnClickListener(view -> {
            Intent intent1 = new Intent(ChatActivity.this,MainActivity.class);
            startActivity(intent1);
        });

        buttonSend.setOnClickListener(view -> {
            String message = editTextMsg.getText().toString();
            if(!editTextMsg.equals("")) {
                sendMessage(message);
            }
            editTextMsg.setText("");
        });

        rvChat = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        msgList =new ArrayList<>();
        msgAdapter = new MessageAdapter(msgList,userName);
        rvChat.setAdapter(msgAdapter);
        getMsg();

    }

    public void getMsg() {
        Log.i("ChatActivity getmsg",userName);
        reference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Message msg = snapshot.getValue(Message.class);
                    msgList.add(msg);
                    msgAdapter.setMsgList(msgList);
                rvChat.scrollToPosition(msgList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void sendMessage(String message) {

        String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
        Map<String,Object> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("from",userName);
        reference.child("Messages").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("Messages").child(otherName).child(userName).child(key).setValue(messageMap);
                }
            }
        });
    }
}
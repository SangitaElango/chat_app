package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends RecyclerView.Adapter<Adapter.ChatViewHolder> {

    private List<User> userList;
    private Context context;
    private String userName;

    public Adapter(List<User> userList, Context context, String userName) {
        this.userList = userList;
        this.context = context;
        this.userName = userName;
    }

    public void setUserName(String userName) {
        Log.i("adapter",userName);
        this.userName = userName;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design,parent,false);
        Log.i("adapter oncreate",userList.toString());
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        User user = userList.get(position);
        holder.textViewName.setText(user.getUserName());
        Log.i("onBindViewHolder",user.getUserName());
        if(user.getImage().equals("null")){
            holder.imageMain.setImageResource(R.drawable.account);
        }
        else {
            Picasso.get().load(user.getImage()).into(holder.imageMain);
        }

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context,ChatActivity.class);
            intent.putExtra("otherName",user.getUserName());
            intent.putExtra("userName",userName);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        Log.i("adapter userlist size",""+userList.size());
        return userList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageMain;
        private TextView textViewName;
        private CardView cardView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMain = itemView.findViewById(R.id.imageMain);
            textViewName = itemView.findViewById(R.id.textViewName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

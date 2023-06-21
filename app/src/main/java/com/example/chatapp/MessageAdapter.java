package com.example.chatapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<Message> msgList;
    String userName;
    boolean status;
    int send;
    int receive;

    public MessageAdapter(List<Message> msgList, String userName) {
        this.msgList = msgList;
        this.userName = userName;
        send = 1;
        receive =2;
        status = false;
    }

    public void setMsgList(List<Message> msgList) {
        this.msgList = msgList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_receive,parent,false);
        }
        Log.i("Adapter onCreate",""+viewType);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.textViewMsg.setText(msgList.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewMsg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            if(status) {
                textViewMsg = itemView.findViewById(R.id.textViewSend);
            }
            else{
                textViewMsg = itemView.findViewById(R.id.textViewReceived);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(msgList.get(position).getFrom().equals(userName)){
            status = true;
            return send;
        }
        else{
            //status = false;
            return receive;
        }
    }
}

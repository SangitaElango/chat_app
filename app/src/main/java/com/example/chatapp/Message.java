package com.example.chatapp;

public class Message {
    private String message;
    private String from;

    public Message(String msg, String from) {
        this.message = msg;
        this.from = from;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

package com.pac.ovum.ui.chatbot;

public class ChatMessage {
    private String message;
    private String time;
    private boolean isUser; // true if user message, false if bot message

    public ChatMessage(String message, String time, boolean isUser) {
        this.message = message;
        this.time = time;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public boolean isUser() {
        return isUser;
    }
}
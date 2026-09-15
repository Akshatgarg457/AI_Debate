package com.project.debatepartner.model;

import java.time.LocalDateTime;

public class Message {

    private String sender;
    private String content;
    private LocalDateTime timestamp;

    // ================= CONSTRUCTORS =================

    public Message() {
    }

    public Message(String sender, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    // ================= GETTERS =================

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // ================= SETTERS =================

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
package com.project.debatepartner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "debates")
public class Debate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String userArgument;

    @Column(columnDefinition = "TEXT")
    private String aiArgument;

    private String result;
    private String winner; // 🔥 NEW FIELD

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ===== GETTERS & SETTERS =====

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserArgument() {
        return userArgument;
    }

    public void setUserArgument(String userArgument) {
        this.userArgument = userArgument;
    }

    public String getAiArgument() {
        return aiArgument;
    }

    public void setAiArgument(String aiArgument) {
        this.aiArgument = aiArgument;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
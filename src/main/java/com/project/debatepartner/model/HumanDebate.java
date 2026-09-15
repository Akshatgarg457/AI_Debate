package com.project.debatepartner.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "human_debates")
public class HumanDebate {

    @Id
    private String id;

    private String type = "HUMAN";

    private String roomId;
    private String topic;

    private String player1;
    private String player2;

    private String player1Stance;
    private String player2Stance;

    private List<Message> messages = new ArrayList<>();

    private String winner;
    private String result;

    private String status = "ACTIVE";

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;

    // =========================
    // CONSTRUCTOR
    // =========================

    public HumanDebate() {
        this.createdAt = LocalDateTime.now();
        this.startedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // =========================
    // GETTERS
    // =========================

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getTopic() {
        return topic;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getPlayer1Stance() {
        return player1Stance;
    }

    public String getPlayer2Stance() {
        return player2Stance;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getWinner() {
        return winner;
    }

    public String getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =========================
    // SETTERS
    // =========================

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void setPlayer1Stance(String player1Stance) {
        this.player1Stance = player1Stance;
    }

    public void setPlayer2Stance(String player2Stance) {
        this.player2Stance = player2Stance;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // =========================
    // ADD MESSAGE
    // =========================

    public void addMessage(String sender, String content) {

        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException(
                    "Completed debates cannot be modified."
            );
        }

        Message message = new Message(
                sender,
                content,
                LocalDateTime.now()
        );

        this.messages.add(message);
    }

    // =========================
    // COMPLETE DEBATE
    // =========================

    public void completeDebate(String winner, String result) {

        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException(
                    "Debate is already completed."
            );
        }

        this.winner = winner;
        this.result = result;
        this.status = "COMPLETED";
        this.endedAt = LocalDateTime.now();
    }
}
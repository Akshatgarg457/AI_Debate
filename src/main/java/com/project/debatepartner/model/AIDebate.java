package com.project.debatepartner.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "ai_debates")
public class AIDebate {

    @Id
    private String id;

    private String type = "AI";

    private String username;
    private String topic;
    private String userStance;
    private String aiName = "Gemini";

    private List<Message> messages = new ArrayList<>();

    private String winner;
    private String result;

    private Integer userScore;
    private Integer aiScore;

    private String status = "ACTIVE";

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AIDebate() {
        this.createdAt = LocalDateTime.now();
        this.startedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getTopic() {
        return topic;
    }

    public String getUserStance() {
        return userStance;
    }

    public String getAiName() {
        return aiName;
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

    public Integer getUserScore() {
        return userScore;
    }

    public Integer getAiScore() {
        return aiScore;
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

    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setUserStance(String userStance) {
        this.userStance = userStance;
    }

    public void setAiName(String aiName) {
        this.aiName = aiName;
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

    public void setUserScore(Integer userScore) {
        this.userScore = userScore;
    }

    public void setAiScore(Integer aiScore) {
        this.aiScore = aiScore;
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

    // =========================================================
    // ADD MESSAGE
    // =========================================================

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

    // =========================================================
    // COMPLETE DEBATE
    // =========================================================

    public void completeDebate(
            String winner,
            String result,
            Integer userScore,
            Integer aiScore) {

        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException(
                    "Debate is already completed."
            );
        }

        this.winner = winner;
        this.result = result;

        this.userScore = userScore;
        this.aiScore = aiScore;

        this.status = "COMPLETED";

        this.endedAt = LocalDateTime.now();
    }
}
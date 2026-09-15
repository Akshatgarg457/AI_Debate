package com.project.debatepartner.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {

    private String roomId;
    private String topic;
    private String creator;
    private List<String> players = new CopyOnWriteArrayList<>();
    private boolean ended = false;
    private long createdAt = System.currentTimeMillis();

    // =========================
    // CONSTRUCTORS
    // =========================

    public Room() {
    }

    public Room(String roomId, String topic, String creator) {
        this.roomId = roomId;
        this.topic = topic;
        this.creator = creator;

        if (creator != null && !creator.trim().isEmpty()) {
            this.players.add(creator.trim());
        }
    }

    public Room(
            String roomId,
            String topic,
            String creator,
            List<String> players,
            boolean ended,
            long createdAt
    ) {
        this.roomId = roomId;
        this.topic = topic;
        this.creator = creator;
        this.players = players;
        this.ended = ended;
        this.createdAt = createdAt;
    }

    // =========================
    // ADD PLAYER
    // =========================

    public synchronized boolean addPlayer(String username) {

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String cleanUser = username.trim();

        if (!players.contains(cleanUser)) {

            if (players.size() >= 2) {
                return false;
            }

            players.add(cleanUser);
        }

        return true;
    }

    // =========================
    // REMOVE PLAYER
    // =========================

    public synchronized void removePlayer(String username) {

        if (username != null) {
            players.remove(username.trim());
        }
    }

    // =========================
    // GETTERS
    // =========================

    public String getRoomId() {
        return roomId;
    }

    public String getTopic() {
        return topic;
    }

    public String getCreator() {
        return creator;
    }

    public List<String> getPlayers() {
        return players;
    }

    public boolean isEnded() {
        return ended;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // =========================
    // SETTERS
    // =========================

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
package com.project.debatepartner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.debatepartner.model.HumanDebate;
import com.project.debatepartner.model.Room;
import com.project.debatepartner.repository.HumanDebateRepository;
import com.project.debatepartner.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DebateWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RoomService roomService;

    @Autowired
    private HumanDebateRepository humanDebateRepository;

    // =========================================================
    // ROOM -> ACTIVE WEBSOCKET SESSIONS
    // =========================================================

    private final Map<String, Set<WebSocketSession>> roomSessions =
            new ConcurrentHashMap<>();

    // =========================================================
    // SESSION ID -> USERNAME
    // =========================================================

    private final Map<String, String> sessionUsernames =
            new ConcurrentHashMap<>();

    // =========================================================
    // SESSION ID -> ROOM ID
    // =========================================================

    private final Map<String, String> sessionRooms =
            new ConcurrentHashMap<>();

    // =========================================================
    // CONNECTION ESTABLISHED
    // =========================================================

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        // WebSocket connection is established.
        // We wait for the JOIN message before assigning the user to a room.
    }

    // =========================================================
    // HANDLE MESSAGE
    // =========================================================

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) throws Exception {

        Map<String, Object> payload =
                objectMapper.readValue(message.getPayload(), Map.class);

        String type = (String) payload.get("type");
        String roomId = (String) payload.get("roomId");
        String username = (String) payload.get("username");

        if (type == null || roomId == null) {
            return;
        }

        switch (type) {

            case "JOIN":

                handleJoin(
                        session,
                        roomId,
                        username,
                        (String) payload.get("stance")
                );

                break;

            case "CHAT":

                handleChat(
                        roomId,
                        username,
                        (String) payload.get("message")
                );

                break;

            case "END_DEBATE":

                handleEndDebate(
                        roomId,
                        username,
                        (String) payload.get("winner"),
                        (String) payload.get("reason")
                );

                break;

            default:

                break;
        }
    }

    // =========================================================
    // JOIN DEBATE
    // =========================================================

    private void handleJoin(
            WebSocketSession session,
            String roomId,
            String username,
            String stance) throws IOException {

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        username = username.trim();

        // -----------------------------------------------------
        // Store session information
        // -----------------------------------------------------

        sessionUsernames.put(
                session.getId(),
                username
        );

        sessionRooms.put(
                session.getId(),
                roomId
        );

        // -----------------------------------------------------
        // Add session to room
        // -----------------------------------------------------

        roomSessions
                .computeIfAbsent(
                        roomId,
                        k -> new CopyOnWriteArraySet<>()
                )
                .add(session);

        // -----------------------------------------------------
        // Add player to in-memory room
        // -----------------------------------------------------

        boolean joined = roomService.joinRoom(
                roomId,
                username
        );

        if (!joined) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "Unable to join the room. The room may be full or unavailable."
            );

            sendToSession(
                    session,
                    errorResponse
            );

            return;
        }

        // -----------------------------------------------------
        // Get room
        // -----------------------------------------------------

        Room room = roomService.getRoom(roomId);

        if (room == null) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "Room not found."
            );

            sendToSession(
                    session,
                    errorResponse
            );

            return;
        }

        // -----------------------------------------------------
        // Check if debate has already ended
        // -----------------------------------------------------

        if (room.isEnded()) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "This debate has already ended."
            );

            sendToSession(
                    session,
                    errorResponse
            );

            return;
        }

        String topic = room.getTopic();

        List<String> players =
                room.getPlayers();

        // =====================================================
        // CREATE MONGODB HUMAN DEBATE
        // =====================================================

        HumanDebate humanDebate =
                humanDebateRepository
                        .findByRoomId(roomId)
                        .orElse(null);

        // -----------------------------------------------------
        // If this is the first player, create the document
        // -----------------------------------------------------

        if (humanDebate == null) {

            humanDebate =
                    new HumanDebate();

            humanDebate.setRoomId(
                    roomId
            );

            humanDebate.setTopic(
                    topic
            );

            humanDebate.setPlayer1(
                    username
            );

            humanDebate.setPlayer1Stance(
                    stance
            );

            humanDebateRepository.save(
                    humanDebate
            );

        } else {

            // -------------------------------------------------
            // Second player joins
            // -------------------------------------------------

            if (humanDebate.getPlayer2() == null
                    && !username.equals(humanDebate.getPlayer1())) {

                humanDebate.setPlayer2(
                        username
                );

                humanDebate.setPlayer2Stance(
                        stance
                );

                humanDebateRepository.save(
                        humanDebate
                );
            }
        }

        // =====================================================
        // JOIN RESPONSE
        // =====================================================

        Map<String, Object> joinResponse =
                new HashMap<>();

        joinResponse.put(
                "type",
                "JOINED"
        );

        joinResponse.put(
                "username",
                username
        );

        joinResponse.put(
                "topic",
                topic
        );

        joinResponse.put(
                "players",
                players
        );

        joinResponse.put(
                "playerCount",
                players.size()
        );

        joinResponse.put(
                "stance",
                stance
        );

        broadcastToRoom(
                roomId,
                joinResponse
        );
    }

    // =========================================================
    // CHAT / ARGUMENT
    // =========================================================

    private void handleChat(
            String roomId,
            String username,
            String chatMessage) throws IOException {

        if (username == null
                || username.trim().isEmpty()) {

            return;
        }

        if (chatMessage == null
                || chatMessage.trim().isEmpty()) {

            return;
        }

        username = username.trim();

        chatMessage = chatMessage.trim();

        // -----------------------------------------------------
        // Get room
        // -----------------------------------------------------

        Room room =
                roomService.getRoom(roomId);

        if (room == null) {

            return;
        }

        // -----------------------------------------------------
        // Prevent messages after debate ends
        // -----------------------------------------------------

        if (room.isEnded()) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "This debate has already ended."
            );

            broadcastToRoom(
                    roomId,
                    errorResponse
            );

            return;
        }

        // -----------------------------------------------------
        // Find MongoDB debate
        // -----------------------------------------------------

        HumanDebate humanDebate =
                humanDebateRepository
                        .findByRoomIdAndStatus(
                                roomId,
                                "ACTIVE"
                        )
                        .orElse(null);

        if (humanDebate == null) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "Active debate not found."
            );

            broadcastToRoom(
                    roomId,
                    errorResponse
            );

            return;
        }

        // =====================================================
        // SAVE ARGUMENT TO MONGODB
        // =====================================================

        try {

            humanDebate.addMessage(
                    username,
                    chatMessage
            );

            humanDebateRepository.save(
                    humanDebate
            );

        } catch (IllegalStateException e) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "This debate is already completed and cannot be modified."
            );

            broadcastToRoom(
                    roomId,
                    errorResponse
            );

            return;
        }

        // =====================================================
        // SEND CHAT TO BOTH PLAYERS
        // =====================================================

        Map<String, Object> chatResponse =
                new HashMap<>();

        chatResponse.put(
                "type",
                "CHAT"
        );

        chatResponse.put(
                "username",
                username
        );

        chatResponse.put(
                "message",
                chatMessage
        );

        chatResponse.put(
                "timestamp",
                System.currentTimeMillis()
        );

        broadcastToRoom(
                roomId,
                chatResponse
        );
    }

    // =========================================================
    // END DEBATE
    // =========================================================

    private void handleEndDebate(
            String roomId,
            String username,
            String winner,
            String reason) throws IOException {

        // -----------------------------------------------------
        // Get room
        // -----------------------------------------------------

        Room room =
                roomService.getRoom(roomId);

        if (room == null) {
            return;
        }

        // -----------------------------------------------------
        // Prevent ending the same debate twice
        // -----------------------------------------------------

        if (room.isEnded()) {

            Map<String, Object> errorResponse =
                    new HashMap<>();

            errorResponse.put(
                    "type",
                    "ERROR"
            );

            errorResponse.put(
                    "message",
                    "This debate has already ended."
            );

            broadcastToRoom(
                    roomId,
                    errorResponse
            );

            return;
        }

        // -----------------------------------------------------
        // Mark in-memory room as ended
        // -----------------------------------------------------

        room.setEnded(true);

        // -----------------------------------------------------
        // Find active MongoDB debate
        // -----------------------------------------------------

        HumanDebate humanDebate =
                humanDebateRepository
                        .findByRoomIdAndStatus(
                                roomId,
                                "ACTIVE"
                        )
                        .orElse(null);

        if (humanDebate != null) {

            String finalWinner =
                    winner;

            if (finalWinner == null
                    || finalWinner.trim().isEmpty()) {

                finalWinner = "DRAW";
            }

            String finalReason =
                    reason;

            if (finalReason == null
                    || finalReason.trim().isEmpty()) {

                finalReason = "Debate completed.";
            }

            // =================================================
            // COMPLETE DEBATE
            // =================================================

            humanDebate.completeDebate(
                    finalWinner,
                    finalReason
            );

            // =================================================
            // SAVE COMPLETED DEBATE
            // =================================================

            humanDebateRepository.save(
                    humanDebate
            );
        }

        // =====================================================
        // END RESPONSE
        // =====================================================

        Map<String, Object> endResponse =
                new HashMap<>();

        endResponse.put(
                "type",
                "ENDED"
        );

        endResponse.put(
                "endedBy",
                username
        );

        endResponse.put(
                "winner",
                winner
        );

        endResponse.put(
                "reason",
                reason
        );

        endResponse.put(
                "readOnly",
                true
        );

        broadcastToRoom(
                roomId,
                endResponse
        );
    }

    // =========================================================
    // CONNECTION CLOSED
    // =========================================================

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) throws Exception {

        String sessionId =
                session.getId();

        String username =
                sessionUsernames.remove(
                        sessionId
                );

        String roomId =
                sessionRooms.remove(
                        sessionId
                );

        // -----------------------------------------------------
        // Remove session from room
        // -----------------------------------------------------

        if (roomId != null) {

            Set<WebSocketSession> sessions =
                    roomSessions.get(roomId);

            if (sessions != null) {

                sessions.remove(session);

                if (sessions.isEmpty()) {

                    roomSessions.remove(
                            roomId
                    );
                }
            }

            // -------------------------------------------------
            // Remove player from in-memory room
            // -------------------------------------------------

            if (username != null) {

                roomService.removePlayer(
                        roomId,
                        username
                );

                Room room =
                        roomService.getRoom(
                                roomId
                        );

                int count =
                        room != null
                                ? room.getPlayers().size()
                                : 0;

                // -------------------------------------------------
                // Notify remaining player
                // -------------------------------------------------

                Map<String, Object> leftResponse =
                        new HashMap<>();

                leftResponse.put(
                        "type",
                        "LEFT"
                );

                leftResponse.put(
                        "username",
                        username
                );

                leftResponse.put(
                        "playerCount",
                        count
                );

                broadcastToRoom(
                        roomId,
                        leftResponse
                );
            }
        }
    }

    // =========================================================
    // SEND TO ONE SESSION
    // =========================================================

    private void sendToSession(
            WebSocketSession session,
            Map<String, Object> messageMap)
            throws IOException {

        if (session == null
                || !session.isOpen()) {

            return;
        }

        String json =
                objectMapper.writeValueAsString(
                        messageMap
                );

        TextMessage textMessage =
                new TextMessage(json);

        synchronized (session) {

            session.sendMessage(
                    textMessage
            );
        }
    }

    // =========================================================
    // BROADCAST TO ROOM
    // =========================================================

    private void broadcastToRoom(
            String roomId,
            Map<String, Object> messageMap)
            throws IOException {

        Set<WebSocketSession> sessions =
                roomSessions.get(roomId);

        if (sessions == null
                || sessions.isEmpty()) {

            return;
        }

        String json =
                objectMapper.writeValueAsString(
                        messageMap
                );

        TextMessage textMessage =
                new TextMessage(json);

        for (WebSocketSession session : sessions) {

            if (session.isOpen()) {

                synchronized (session) {

                    session.sendMessage(
                            textMessage
                    );
                }
            }
        }
    }
}
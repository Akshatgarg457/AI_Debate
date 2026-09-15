package com.project.debatepartner.repository;

import com.project.debatepartner.model.HumanDebate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HumanDebateRepository
        extends MongoRepository<HumanDebate, String> {

    // History for a player
    List<HumanDebate> findByPlayer1OrPlayer2OrderByCreatedAtDesc(
            String player1,
            String player2
    );

    // Find the debate belonging to a live room
    Optional<HumanDebate> findByRoomId(String roomId);

    // Find active debate for a room
    Optional<HumanDebate> findByRoomIdAndStatus(
            String roomId,
            String status
    );
}
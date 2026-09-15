package com.project.debatepartner.repository;

import com.project.debatepartner.model.AIDebate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AIDebateRepository extends MongoRepository<AIDebate, String> {

    // AI Debate History
    List<AIDebate> findByUsernameOrderByCreatedAtDesc(String username);

    // View details of a specific AI debate
    Optional<AIDebate> findByIdAndUsername(String id, String username);

    // Find active debates for a user
    List<AIDebate> findByUsernameAndStatus(
            String username,
            String status
    );
}
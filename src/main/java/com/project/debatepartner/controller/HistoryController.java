package com.project.debatepartner.controller;

import com.project.debatepartner.model.AIDebate;
import com.project.debatepartner.model.HumanDebate;
import com.project.debatepartner.repository.AIDebateRepository;
import com.project.debatepartner.repository.HumanDebateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private AIDebateRepository aiDebateRepository;

    @Autowired
    private HumanDebateRepository humanDebateRepository;

    @GetMapping("/ai")
    public List<AIDebate> getAIDebateHistory(
            @RequestParam String username) {

        return aiDebateRepository
                .findByUsernameOrderByCreatedAtDesc(username);
    }

    @GetMapping("/human")
    public List<HumanDebate> getHumanDebateHistory(
            @RequestParam String username) {

        return humanDebateRepository
                .findByPlayer1OrPlayer2OrderByCreatedAtDesc(
                        username,
                        username
                );
    }

    @GetMapping("/ai/{debateId}")
    public AIDebate getAIDebateDetails(
            @PathVariable String debateId,
            @RequestParam String username) {

        return aiDebateRepository
                .findByIdAndUsername(
                        debateId,
                        username
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI debate not found."
                        )
                );
    }

    @GetMapping("/human/{debateId}")
    public HumanDebate getHumanDebateDetails(
            @PathVariable String debateId,
            @RequestParam String username) {

        HumanDebate debate =
                humanDebateRepository
                        .findById(debateId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Human debate not found."
                                )
                        );

        boolean isParticipant =
                username.equals(debate.getPlayer1())
                || username.equals(debate.getPlayer2());

        if (!isParticipant) {
            throw new RuntimeException(
                    "You are not authorized to view this debate."
            );
        }

        return debate;
    }
}
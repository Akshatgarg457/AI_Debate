package com.project.debatepartner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/forget")
    public String forgetPage() {
        return "forget";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/topic")
    public String topicPage() {
        return "topic";
    }

    @GetMapping("/debate")
    public String debatePage() {
        return "debate";
    }

    @GetMapping("/ai-history")
    public String aiHistoryPage() {
        return "ai-history";
    }

    @GetMapping("/create-room")
    public String createRoomPage() {
        return "create-room";
    }

    @GetMapping("/human-debate")
    public String humanDebatePage() {
        return "human-debate";
    }

    @GetMapping("/human-history")
    public String humanHistoryPage() {
        return "human-history";
    }
}
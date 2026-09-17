package com.project.debatepartner.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {


    @GetMapping("/")
    public String showLoginPage(HttpSession session) {

        if (isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        return "login";
    }


    @GetMapping("/login")
    public String loginPage(HttpSession session) {

        if (isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        return "login";
    }


    @GetMapping("/signup")
    public String signupPage(HttpSession session) {

        if (isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        return "signup";
    }


    @GetMapping("/forget")
    public String forgetPage(HttpSession session) {

        if (isLoggedIn(session)) {
            return "redirect:/dashboard";
        }

        return "forget";
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "dashboard";
    }


    @GetMapping("/topic")
    public String topicPage(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "topic";
    }


    @GetMapping("/debate")
    public String debatePage(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "debate";
    }


    @GetMapping("/ai-history")
    public String aiHistoryPage(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "ai-history";
    }


    @GetMapping("/create-room")
    public String createRoomPage(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "create-room";
    }


    @GetMapping("/human-debate")
    public String humanDebatePage(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "human-debate";
    }


    @GetMapping("/human-history")
    public String humanHistoryPage(HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "human-history";
    }


    private boolean isLoggedIn(HttpSession session) {

        return session.getAttribute("username") != null;
    }
}
package com.project.debatepartner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Open login page when user visits "/"
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
    @GetMapping("/debate")
    public String debate() {
        return "debate";
    }
    @GetMapping("/topic")
    public String topicPage() {
        return "topic";
    }
    @GetMapping("/result")
    public String resultPage() {
        return "result";
    }
}  
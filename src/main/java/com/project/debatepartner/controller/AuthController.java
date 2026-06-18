package com.project.debatepartner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.project.debatepartner.model.User;
import com.project.debatepartner.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ================= SIGNUP =================
    @PostMapping("/signup")
    public String signup(@RequestParam String fullName,
                         @RequestParam String email,
                         @RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         @RequestParam String securityQ,
                         @RequestParam String answer) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/signup?error=password";
        }

        if (userRepository.findByUsername(username) != null) {
            return "redirect:/signup?error=username";
        }

        if (userRepository.findByEmail(email) != null) {
            return "redirect:/signup?error=email";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setSecurityQ(securityQ);
        user.setAnswer(answer.toLowerCase()); // important

        userRepository.save(user);

        return "redirect:/login?success=true";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/dashboard";
        }

        return "redirect:/login?error=true";
    }

    // ================= GET SECURITY QUESTION =================
    @GetMapping("/get-question")
    @ResponseBody
    public String getSecurityQuestion(@RequestParam String username) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "NOT_FOUND";
        }

        return user.getSecurityQ();
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username,
                               @RequestParam String securityQ,
                               @RequestParam String answer,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "redirect:/forget?error=user";
        }

        if (!user.getSecurityQ().equals(securityQ) ||
            !user.getAnswer().equals(answer.toLowerCase())) {
            return "redirect:/forget?error=answer";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/forget?error=password";
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return "redirect:/login?reset=true";
    }
}
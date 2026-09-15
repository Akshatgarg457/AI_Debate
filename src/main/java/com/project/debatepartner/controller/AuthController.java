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


    // =====================================================
    // SIGNUP
    // =====================================================

    @PostMapping("/signup")
    public String signup(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String securityQ,
            @RequestParam String answer) {

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            return "redirect:/signup?error=password";
        }

        // Check duplicate username
        if (userRepository.findByUsername(username) != null) {
            return "redirect:/signup?error=username";
        }

        // Check duplicate email
        if (userRepository.findByEmail(email) != null) {
            return "redirect:/signup?error=email";
        }

        // Create new user
        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setSecurityQ(securityQ);
        user.setAnswer(answer.toLowerCase());

        // Save user to MongoDB
        userRepository.save(user);

        return "redirect:/login?success=true";
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/dashboard";
        }

        return "redirect:/login?error=true";
    }


    // =====================================================
    // GET SECURITY QUESTION
    // =====================================================

    @GetMapping("/get-question")
    @ResponseBody
    public String getSecurityQuestion(
            @RequestParam String username) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "NOT_FOUND";
        }

        return user.getSecurityQ();
    }


    // =====================================================
    // RESET PASSWORD
    // =====================================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String username,
            @RequestParam String securityQ,
            @RequestParam String answer,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        User user = userRepository.findByUsername(username);

        // User doesn't exist
        if (user == null) {
            return "redirect:/forget?error=user";
        }

        // Check security question and answer
        if (!user.getSecurityQ().equals(securityQ)
                || !user.getAnswer().equals(answer.toLowerCase())) {

            return "redirect:/forget?error=answer";
        }

        // Check new password confirmation
        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/forget?error=password";
        }

        // Update password
        user.setPassword(newPassword);

        // Save updated user
        userRepository.save(user);

        return "redirect:/login?reset=true";
    }
}
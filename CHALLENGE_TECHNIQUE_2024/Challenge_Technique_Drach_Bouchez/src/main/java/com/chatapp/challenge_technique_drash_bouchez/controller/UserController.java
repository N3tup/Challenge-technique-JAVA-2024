package com.chatapp.challenge_technique_drash_bouchez.controller;

import com.chatapp.challenge_technique_drash_bouchez.model.User;
import com.chatapp.challenge_technique_drash_bouchez.repository.UserRepository;
import com.chatapp.challenge_technique_drash_bouchez.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Display the user registration form
    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup"; // Returns signup.html view
    }

    // Handle the user registration form submission
    @PostMapping("/signup")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model
    ) {
        // Check if the username or email is already taken
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already taken.");
            return "signup";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already taken.");
            return "signup";
        }

        // Hash the password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        // Create a new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        // Save the new user
        userRepository.save(user);

        // Redirect to a success page or login page
        return "/page_confirmation"; // Or use a confirmation page
    }

    // Display the user login form
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("error", null);
        return "page_connection"; // Returns page_connection.html view
    }

    // Handle user login

    // Handle user login
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model
    ) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                model.addAttribute("username", username);
                return "redirect:/chat?username=" + username; // Redirect to the chat page or dashboard
            } else {
                model.addAttribute("error", "Invalid password.");
                return "/page_connection"; // Return home page with error
            }
        } else {
            model.addAttribute("error", "Username does not exist.");
            return "/page_connection"; // Return home page with error
        }

    }

    // Handle user logout
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/"; // Redirect to the homepage or login page
    }

    @GetMapping("/forgot_password")
    public String forgotPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "forgot_password";
    }

    // Handle the email submission for password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> handleForgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> user = userRepository.findByEmail(email);

        Map<String, String> response = new HashMap<>();
        if (user.isPresent()) {
            response.put("status", "success");
            response.put("message", "Email found");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/reset_password")
    public String resetPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "page_reset_password";
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleResetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String newPassword = payload.get("newPassword");

        // Create response map
        Map<String, String> response = new HashMap<>();

        // Find the user by email
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            // Hash the new password using BCrypt
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);

            // Update the user's password
            User foundUser = user.get();
            foundUser.setPassword(hashedPassword);

            // Save the updated user information
            userRepository.save(foundUser);

            // Send success response
            response.put("status", "success");
            response.put("message", "Password updated successfully");
            return ResponseEntity.ok(response);
        } else {
            // Send error response
            response.put("status", "error");
            response.put("message", "Email address not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PostMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> user = userRepository.findByEmail(email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", user.isPresent());

        return ResponseEntity.ok(response);
    }

    @GetMapping("reset-password-success")
    public String resetPasswordSuccess() {
        return "password_reset_success";
    }


}

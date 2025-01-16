package com.chatapp.discoghd.controller;

import com.chatapp.discoghd.model.User;
import com.chatapp.discoghd.repository.UserRepository;
import com.chatapp.discoghd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}

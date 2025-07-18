package com.example.leave_application.controller;

import com.example.leave_application.dto.ForgotPasswordDto;
import com.example.leave_application.dto.GenericMessageDto;
import com.example.leave_application.dto.LoginDto;
import com.example.leave_application.model.ForgotPasswordRequest;
import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.ForgotPasswordRequestRepository;
import com.example.leave_application.repository.UserRepository;
import com.example.leave_application.services.AuthService;
import com.example.leave_application.services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

import static com.example.leave_application.exception.CommonExceptions.validationError;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authservice;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String , Object> request) {
        // Logic for user registration
        try{
            String name = (String) request.get("name");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String department = (String) request.get("department");
            String designation = (String) request.get("designation");
            String employeeId = (String) request.get("employeeId");
            String phoneNumber = (String) request.get("phoneNumber");
            String sex =  request.get("sex").toString().toUpperCase();
            List<String> rolesString= (List<String>) request.get("roles");
            Set<RoleType> roles = rolesString.stream()
                    .map(role -> RoleType.valueOf(role.toUpperCase()))
                    .collect(Collectors.toSet());
            String response =authservice.registerUser(name, email, password, department, designation, employeeId, phoneNumber,sex, roles);
            return ResponseEntity.ok("User registered successfully");
        }
        catch (Exception e) {
            String message = "User Already exists witht this email";
            return ResponseEntity.badRequest().body("message: " + message);
        }

    }
    @PostMapping("/login")
    public ResponseEntity<LoginDto> loginUser(@RequestBody Map<String, String> request) {
        // Logic for user login
        String email = request.get("email");
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String password = request.get("password");
        String token = authservice.loginUser(email, password);
        System.err.println("Token: " + token);
        User user = userRepository.findUserByEmail(email).orElseThrow();
        return ResponseEntity.ok(new LoginDto(token, email, user.getRoles()));

    }

    // endpoint for logout
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
           SecurityContextHolder.clearContext();
        }

        return ResponseEntity.ok("User logged out successfully");
    }


    @GetMapping("/base-url")
    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +
                request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GenericMessageDto> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(new GenericMessageDto("Email sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericMessageDto> newPassword(@Valid @RequestBody ForgotPasswordDto data) {
        String newPassword = data.getNewPassword();
        String token = data.getToken();
        UUID uuid = UUID.fromString(token);
        authService.resetPassword(uuid, newPassword);
        return  ResponseEntity.ok(new GenericMessageDto("Password changed successfully"));
    }
}

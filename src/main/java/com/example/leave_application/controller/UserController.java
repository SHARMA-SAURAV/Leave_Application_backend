package com.example.leave_application.controller;

import com.example.leave_application.dto.ForgotPasswordDto;
import com.example.leave_application.dto.GenericMessageDto;
import com.example.leave_application.model.ForgotPasswordRequest;
import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.ForgotPasswordRequestRepository;
import com.example.leave_application.repository.UserRepository;
import com.example.leave_application.services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.example.leave_application.exception.CommonExceptions.validationError;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

//    @GetMapping("/users")
//    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role, Authentication auth) {
//        System.out.println("Authenticated user: " + auth.getName());
//        RoleType roleType = RoleType.valueOf(role.toUpperCase());
//        List<User> users = userRepository.findByRole(roleType);
//        return ResponseEntity.
//       ok(users);
//    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role, Authentication auth) {
        System.out.println("Authenticated user: " + auth.getName());
        RoleType roleType = RoleType.valueOf(role.toUpperCase());
        List<User> users = userRepository.findByRolesContaining(roleType);
        return ResponseEntity.ok(users);
    }
}

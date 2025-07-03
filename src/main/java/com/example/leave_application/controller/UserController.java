package com.example.leave_application.controller;

import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
//        return ResponseEntity.ok(users);
//    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role, Authentication auth) {
        System.out.println("Authenticated user: " + auth.getName());
        RoleType roleType = RoleType.valueOf(role.toUpperCase());
        List<User> users = userRepository.findByRolesContaining(roleType);
        return ResponseEntity.ok(users);
    }


}

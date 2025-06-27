package com.example.leave_application.services;

import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.UserRepository;
import com.example.leave_application.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository; // Assuming UserRepository is an interface extending JpaRepository<User, Long>
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil; // Assuming JwtUtil is a utility class for JWT operations
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(String name, String email, String password, String department, String designation, String employeeId, String phoneNumber,String sex, Set<RoleType> roles) {
        // Logic for user registration
        // This method should contain the logic to save the user details in the database
        // For now, we will just return a success message
        Optional<User> existingUser = userRepository.findUserByEmail(email);
        if (existingUser.isPresent()) {
            return "User with this email already exists";
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Assuming passwordEncoder is an instance of PasswordEncoder that encodes the password
        user.setDepartment(department);
        user.setDesignation(designation);
        user.setEmployeeId(employeeId);
        user.setPhoneNumber(phoneNumber);
        user.setSex(sex.toUpperCase());
        user.setRoles(roles);
        userRepository.save(user); // Assuming userRepository is an instance of UserRepository that extends JpaRepository<User, Long>

        return "User registered successfully";
    }

    // Now create for login method
    public String loginUser(String email, String password) {
        // Logic for user login
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return jwtUtil.generateToken(email);
        // This method should contain the logic to authenticate the user


    }
}

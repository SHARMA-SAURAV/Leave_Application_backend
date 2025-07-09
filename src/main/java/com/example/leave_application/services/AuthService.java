package com.example.leave_application.services;

import com.example.leave_application.model.ForgotPasswordRequest;
import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.ForgotPasswordRequestRepository;
import com.example.leave_application.repository.UserRepository;
import com.example.leave_application.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.example.leave_application.exception.CommonExceptions.validationError;

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
    @Autowired
    private ForgotPasswordRequestRepository forgotPasswordRequestRepository;
    @Autowired
    private EmailService emailService;

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

    @Transactional
    public void forgotPassword(String userEmail) {
        User user = userRepository.findUserByEmail(userEmail).orElseThrow(validationError("Email not found"));
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setUser(user);
        forgotPasswordRequestRepository.save(request);

        emailService.sendEmail(
                user.getEmail(),
                "Password change request",
                "Dear " + user.getName() + ",\n\nHere is the link to change your password: " + "http://localhost:5173/reset-password/?token=" + request.getId().toString()  + ".\n\nRegards,\nLeave Management System"
        );
    }

    @Transactional
    public void resetPassword(UUID requestId, String newPassword) {
        ForgotPasswordRequest req = forgotPasswordRequestRepository.findByIdAndUsedFalse(requestId).orElseThrow(validationError("Token not found"));
        req.getUser().setPassword(passwordEncoder.encode(newPassword));
        req.setUsed(true);
        userRepository.save(req.getUser());
        forgotPasswordRequestRepository.save(req);
    }
}

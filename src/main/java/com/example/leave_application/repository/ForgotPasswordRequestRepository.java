package com.example.leave_application.repository;

import com.example.leave_application.model.ForgotPasswordRequest;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.LeaveStatus;
import com.example.leave_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForgotPasswordRequestRepository extends JpaRepository<ForgotPasswordRequest, UUID> {
    public Optional<ForgotPasswordRequest> findByIdAndUsedFalse(UUID id);
}
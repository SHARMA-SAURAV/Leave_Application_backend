package com.example.leave_application.repository;

import com.example.leave_application.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmail(String email);
}
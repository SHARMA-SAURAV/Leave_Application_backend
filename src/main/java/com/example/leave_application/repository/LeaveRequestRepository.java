package com.example.leave_application.repository;

import com.example.leave_application.model.EntrySlip;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.LeaveStatus;
import com.example.leave_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
//    List<LeaveRequest> findByEmail(String email);
//    List<LeaveRequest> findByCurrentLevelAndEmail(String currentLevel, String status);
    List<LeaveRequest> findByRequestedByOrderByAppliedAtDesc(User requestedBy);
    List<LeaveRequest> findByStatusOrderByAppliedAtAsc(LeaveStatus status);
}

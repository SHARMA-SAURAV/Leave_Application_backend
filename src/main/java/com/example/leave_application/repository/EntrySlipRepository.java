package com.example.leave_application.repository;

import com.example.leave_application.model.EntrySlip;
import com.example.leave_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrySlipRepository extends JpaRepository<EntrySlip, Long> {
//    List<EntrySlip> findByEmail(String email);

    List<EntrySlip> findByCreatedBy_Email(String email);

//    List<EntrySlip> findByCurrentLevelAndEmail(String currentLevel, String approverEmail, String status);
    List<EntrySlip> findByCurrentLevelAndApproverEmailAndStatus(String currentLevel, String approverEmail, String status);
    List<EntrySlip> findByCurrentLevelAndStatus(String currentLevel, String status);
    List<EntrySlip> findByCreatedBy(User user);
    List<EntrySlip> findByStatus(String status);


}
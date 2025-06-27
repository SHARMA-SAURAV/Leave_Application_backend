package com.example.leave_application.repository;

import com.example.leave_application.model.EntrySlip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrySlipRepository extends JpaRepository<EntrySlip, Long> {
    List<EntrySlip> findByEmail(String email);
}
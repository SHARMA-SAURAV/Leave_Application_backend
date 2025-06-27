package com.example.leave_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveType type; // FULL_DAY, HALF_DAY

    @Enumerated(EnumType.STRING)
    private LeaveHalfType halfType; // FIRST_HALF, SECOND_HALF

    @Enumerated(EnumType.STRING)
    private LeaveCategory category; // PL, CL, RH

    private String dates; // comma-separated dates
    private String substitute; // FLA assigned
    private String status; // PENDING, APPROVED, REJECTED, etc.
    private String currentLevel; // FLA, SLA, HR

    private LocalDateTime appliedAt;
}


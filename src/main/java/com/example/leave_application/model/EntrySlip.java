package com.example.leave_application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrySlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String reason;
    private LocalDate date;
    @Column(columnDefinition = "TIME")
    private LocalTime inTime;

    @Column(columnDefinition = "TIME")
    private LocalTime outTime;

    private String status;
    private String currentLevel;
    private LocalDateTime appliedAt;

    private String approverEmail; // either selected FLA or SLA
//    private String currentLevel;  // "FLA" or "SLA"
}

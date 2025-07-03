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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    private String reason;
    private LocalDate date;

    @Column(columnDefinition = "TIME")
    private LocalTime inTime;

    @Column(columnDefinition = "TIME")
    private LocalTime outTime;

    private String status;
    private String currentLevel;
    private LocalDateTime appliedAt;

    private String approverEmail; // initial approver selected (FLA or SLA)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_fla_id")
    private User approvedByFLA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_sla_id")
    private User approvedBySLA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_hr_id")
    private User approvedByHR;
}

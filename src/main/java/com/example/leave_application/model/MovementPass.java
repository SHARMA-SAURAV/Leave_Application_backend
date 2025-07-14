package com.example.leave_application.model;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.ISBN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovementPass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    private User flaApprover;

    @ManyToOne(fetch = FetchType.EAGER)
    private User slaApprover;

    @Column(nullable = false, length = 2000)
    private String reason;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private MovementPassStatus status;

    private LocalDateTime flaApprovalAt;
    private LocalDateTime slaApprovalAt;
    private LocalDateTime hrApprovalAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;

    public void validate() {
        if(slaApprover == null && flaApprover == null) throw new ValidationException("Must have at least one approver.");
    }
}

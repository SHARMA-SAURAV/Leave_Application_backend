package com.example.leave_application.dto;

import com.example.leave_application.model.RoleType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class CreateMovementPassDto {
    @NotNull
    @FutureOrPresent
    private LocalDate date;

    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;

    @NotBlank
    private String reason;

    @NotNull
    private RoleType approverRole;

    @AssertTrue(message = "Approver must be either FLA or SLA")
    private boolean isValidApproverRole() {
        return approverRole == RoleType.FLA || approverRole == RoleType.SLA;
    }

    @NotNull
    private Long approverId;
}

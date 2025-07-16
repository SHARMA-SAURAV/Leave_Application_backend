package com.example.leave_application.dto;

import com.example.leave_application.model.LeaveType;
import com.example.leave_application.model.RoleType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class CreateLeaveDto {
    @NotNull
    @FutureOrPresent
    private LocalDate startDate;
    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

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

    @NotEmpty
    private Set<LeaveType> leaveTypes;
    @NotNull
    @Positive
    private int leaveCount;
}

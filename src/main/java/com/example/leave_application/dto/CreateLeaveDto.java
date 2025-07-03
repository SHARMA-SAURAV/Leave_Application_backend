package com.example.leave_application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private Long flaId;
    private Long slaId;

    private boolean plLeave = false;
    private boolean clLeave = false;
    private boolean rhLeave = false;
}

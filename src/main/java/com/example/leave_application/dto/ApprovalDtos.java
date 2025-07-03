package com.example.leave_application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ApprovalDtos {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class FlaApprovalDto {
        Long slaSelected;
        @NotNull
        Boolean isApproved;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class SlaApprovalDto {
        @NotNull
        Boolean isApproved;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class HrApprovalDto {
        @NotNull
        Boolean isApproved;
    }
}

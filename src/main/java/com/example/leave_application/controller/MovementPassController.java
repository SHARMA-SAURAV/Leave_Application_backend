package com.example.leave_application.controller;

import com.example.leave_application.dto.ApprovalDtos;
import com.example.leave_application.dto.CreateLeaveDto;
import com.example.leave_application.dto.CreateMovementPassDto;
import com.example.leave_application.dto.GenericMessageDto;
import com.example.leave_application.model.*;
import com.example.leave_application.repository.UserRepository;
import com.example.leave_application.security.UserDetailsImpl;
import com.example.leave_application.services.EmailService;
import com.example.leave_application.services.MovementPassService;
import com.example.leave_application.services.MovementPassService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.leave_application.exception.CommonExceptions.validationError;

@RestController
@RequestMapping("/api/movement")
public class MovementPassController {

    @Autowired
    private MovementPassService movementPassService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/apply")
    public ResponseEntity<GenericMessageDto> applyPass(@Valid @RequestBody CreateMovementPassDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MovementPass pass = new MovementPass();
        pass.setRequestedBy(userDetails.getUser());
        pass.setReason(data.getReason());
        pass.setDate(data.getDate());
        pass.setStartTime(data.getStartTime());
        pass.setEndTime(data.getEndTime());

        User approver = userRepository.findById(data.getApproverId()).orElseThrow(validationError("Selected Approver Not Found"));
        if(!approver.getRoles().contains(data.getApproverRole())) {
            throw new ValidationException("Selected Approver is not a " + data.getApproverRole().toString());
        }
        if(data.getApproverRole() == RoleType.FLA) {
            pass.setFlaApprover(approver);
            pass.setStatus(MovementPassStatus.FLA);
        } else if(data.getApproverRole() == RoleType.SLA) {
            pass.setSlaApprover(approver);
            pass.setStatus(MovementPassStatus.SLA);
        } else {
            throw new ValidationException("Selected Approver type is not a valid role type");
        }

        movementPassService.generateMovementPass(pass);
        // Send email to selected approver (FLA or SLA)
        if (approver.getEmail() != null) {
            emailService.sendEmail(
                    approver.getEmail(),
                    "Movement Pass Approval Needed",
                    "Dear " + approver.getName() + ",\n\nA movement pass is pending your approval.\n\nRegards,\nLeave Management System"
            );
        }
        return ResponseEntity.ok(new GenericMessageDto("Movement pass submitted."));
    }

//    @GetMapping("/fla/upcoming")
//    public List<MovementPass> getFlaUpcomingLeaves(
//            @RequestParam(required = false) String searchString,
//            @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        System.err.println("Thing: " + (searchString));
//        return movementPassService.getUpcomingLeaves(userDetails.getUser().getId(), null, searchString);
//    }
//
//    @GetMapping("/sla/upcoming")
//    public List<MovementPass> getSlaUpcomingLeaves(
//            @RequestParam(required = false) String searchString,
//            @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return movementPassService.getUpcomingLeaves(null, userDetails.getUser().getId(), searchString);
//    }
//
//    @GetMapping("/hr/upcoming")
//    public List<MovementPass> getHrUpcomingLeaves(
//            @RequestParam(required = false) String searchString,
//            @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return movementPassService.getUpcomingLeaves(null, null, searchString);
//    }

    @GetMapping("/all")
    public List<MovementPass> allRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return movementPassService.getEmployeeMovementPasses(userDetails.getUser());
    }

    @GetMapping("/fla/all")
    public List<MovementPass> flaRequests() {
        return movementPassService.getFlaRequests();
    }

    @PatchMapping("/fla/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveFla(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.FlaPassApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        movementPassService.approvePassByFla(id, data.getSlaSelected(), data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Movement pass approved and sent to SLA";
        else returnMessage = "Movement pass application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @GetMapping("/sla/all")
    public List<MovementPass> slaRequests() {
        return movementPassService.getSlaRequests();
    }

    @PatchMapping("/sla/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveSla(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.SlaPassApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        movementPassService.approvePassBySla(id, data.getIsApproved());
        MovementPass movementPass = movementPassService.getMovementPassById(id); // You may need to implement this method
        String returnMessage;
        if(data.getIsApproved()) {
            // Notify HR for next approval
            User hr = movementPassService.getHrForPass(movementPass); // You may need to implement this method
            if(hr != null && hr.getEmail() != null) {
                emailService.sendEmail(
                        hr.getEmail(),
                        "Movement Pass Approval Needed",
                        "Dear " + hr.getName() + ",\n\nA movement pass is pending your approval.\n\nRegards,\nLeave Management System"
                );
            }
            returnMessage = "Movement Pass approved and sent to HR";
        } else {
            // Notify applicant of rejection
            User applicant = movementPass.getRequestedBy();
            if(applicant != null && applicant.getEmail() != null) {
                emailService.sendEmail(
                        applicant.getEmail(),
                        "Your Movement Pass is Rejected",
                        "Dear " + applicant.getName() + ",\n\nYour Movement Pass has been rejected.\n\nRegards,\nLeave Management System"
                );
            }
            returnMessage = "Movement Pass rejected";
        }
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @GetMapping("/hr/all")
    public List<MovementPass> hrRequests() {
        return movementPassService.getHrRequests();
    }

    @GetMapping("/hr/approved")
    public List<MovementPass> approvedRequests() {
        return movementPassService.getApprovedRequests();
    }

    @PatchMapping("/hr/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveHr(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.HrPassApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        movementPassService.approvePassByHr(id, data.getIsApproved());
        MovementPass movementPass = movementPassService.getMovementPassById(id); // You may need to implement this method
        String returnMessage;
        if(data.getIsApproved()) {
            // Notify applicant of approval
            User applicant = movementPass.getRequestedBy();
            if(applicant != null && applicant.getEmail() != null) {
                emailService.sendEmail(
                        applicant.getEmail(),
                        "Your Movement Pass is Approved",
                        "Dear " + applicant.getName() + ",\n\nYour Movement Pass has been approved.\n\nRegards,\nLeave Management System"
                );
            }
            returnMessage = "Movement Pass approved";
        } else {
            // Notify applicant of rejection
            User applicant = movementPass.getRequestedBy();
            if(applicant != null && applicant.getEmail() != null) {
                emailService.sendEmail(
                        applicant.getEmail(),
                        "Your Movement Pass is Rejected",
                        "Dear " + applicant.getName() + ",\n\nYour Movement Pass has been rejected.\n\nRegards,\nLeave Management System"
                );
            }
            returnMessage = "Movement Pass rejected";
        }
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @GetMapping("/user")
    public ResponseEntity<List<MovementPass>> getUserPasses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MovementPass> passes = movementPassService.getEmployeeMovementPasses(userDetails.getUser());
        return ResponseEntity.ok(passes);
    }

//    @GetMapping("/approved")
//    public List<MovementPass> getApprovedLeaves() {
//        return movementPassService.getAllApprovedLeaves(); // implement this method
//    }


}

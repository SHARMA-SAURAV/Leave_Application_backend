package com.example.leave_application.controller;

import com.example.leave_application.dto.ApprovalDtos;
import com.example.leave_application.dto.CreateLeaveDto;
import com.example.leave_application.dto.GenericMessageDto;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.LeaveStatus;
import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.UserRepository;
import com.example.leave_application.security.UserDetailsImpl;
import com.example.leave_application.services.LeaveService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.leave_application.exception.CommonExceptions.validationError;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/apply")
    public ResponseEntity<GenericMessageDto> applyLeave(@Valid @RequestBody CreateLeaveDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setRequestedBy(userDetails.getUser());
        leaveRequest.setReason(data.getReason());
        leaveRequest.setStartDate(data.getStartDate());
        leaveRequest.setEndDate(data.getEndDate());
        leaveRequest.setPlLeaves(data.getPlLeaves());
        leaveRequest.setClLeaves(data.getClLeaves());
        leaveRequest.setRhLeaves(data.getRhLeaves());
        leaveRequest.setOtherLeaves(data.getOtherLeaves());

        User approver = userRepository.findById(data.getApproverId()).orElseThrow(validationError("Selected Approver Not Found"));
        if(!approver.getRoles().contains(data.getApproverRole())) {
            throw new ValidationException("Selected Approver is not a " + data.getApproverRole().toString());
        }
        if(data.getApproverRole() == RoleType.FLA) {
            leaveRequest.setFlaApprover(approver);
            leaveRequest.setStatus(LeaveStatus.FLA);
        } else if(data.getApproverRole() == RoleType.SLA) {
            leaveRequest.setSlaApprover(approver);
            leaveRequest.setStatus(LeaveStatus.SLA);
        } else {
            throw new ValidationException("Selected Approver type is not a valid role type");
        }

        leaveService.generateLeaveApplication(leaveRequest);
        return ResponseEntity.ok(new GenericMessageDto("Leave request submitted."));
    }

    @GetMapping("/all")
    public List<LeaveRequest> allRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return leaveService.getEmployeeLeaveApplications(userDetails.getUser());
    }

    @GetMapping("/fla/all")
    public List<LeaveRequest> flaRequests() {
        return leaveService.getFlaRequests();
    }

    @PatchMapping("/fla/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveFla(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.FlaApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        leaveService.approveLeaveByFla(id, data.getSlaSelected(), data.getSubstituteSelected(), data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Leave application approved and sent to SLA";
        else returnMessage = "Leave application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @GetMapping("/sla/all")
    public List<LeaveRequest> slaRequests() {
        return leaveService.getSlaRequests();
    }

    @PatchMapping("/sla/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveSla(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.SlaApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        leaveService.approveLeaveBySla(id, data.getSubstituteSelected(), data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Leave application approved and sent to HR";
        else returnMessage = "Leave application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @GetMapping("/hr/all")
    public List<LeaveRequest> hrRequests() {
        return leaveService.getHrRequests();
    }

    @GetMapping("/hr/approved")
    public List<LeaveRequest> approvedRequests() {
        return leaveService.getApprovedRequests();
    }

    @PatchMapping("/hr/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveHr(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.HrApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        leaveService.approveLeaveByHr(id, data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Leave application approved";
        else returnMessage = "Leave application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @GetMapping("/user")
    public ResponseEntity<List<LeaveRequest>> getUserLeaves(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<LeaveRequest> leaves = leaveService.getEmployeeLeaveApplications(userDetails.getUser());
        return ResponseEntity.ok(leaves);
    }

//    @GetMapping("/approved")
//    public List<LeaveRequest> getApprovedLeaves() {
//        return leaveService.getAllApprovedLeaves(); // implement this method
//    }


}


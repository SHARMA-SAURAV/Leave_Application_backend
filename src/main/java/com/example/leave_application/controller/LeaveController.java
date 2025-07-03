package com.example.leave_application.controller;

import com.example.leave_application.dto.ApprovalDtos;
import com.example.leave_application.dto.CreateLeaveDto;
import com.example.leave_application.dto.GenericMessageDto;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.LeaveStatus;
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
        leaveRequest.setPlLeave(data.isPlLeave());
        leaveRequest.setClLeave(data.isClLeave());
        leaveRequest.setRhLeave(data.isRhLeave());

        if(data.getFlaId() != null) {
            leaveRequest.setFlaApprover(userRepository.findById(data.getFlaId()).orElseThrow());
            leaveRequest.setStatus(LeaveStatus.FLA);
        } else if(data.getSlaId() != null) {
            leaveRequest.setSlaApprover(userRepository.findById(data.getSlaId()).orElseThrow());
            leaveRequest.setStatus(LeaveStatus.SLA);
        } else {
            throw new ValidationException("Must have at least one approver");
        }

        leaveService.generateLeaveApplication(leaveRequest);
        return ResponseEntity.ok(new GenericMessageDto("Leave request submitted."));
    }

    @GetMapping("/all")
    public List<LeaveRequest> allRequests(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return leaveService.getEmployeeLeaveApplications(userDetails.getUser());
    }

    @PatchMapping("/fla/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveFla(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.FlaApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        leaveService.approveLeaveByFla(id, data.getSlaSelected(), data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Leave application approved and sent to SLA";
        else returnMessage = "Leave application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @PatchMapping("/sla/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveSla(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.FlaApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        leaveService.approveLeaveBySla(id, data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Leave application approved and sent to HR";
        else returnMessage = "Leave application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

    @PatchMapping("/hr/approve/{id}")
    public ResponseEntity<GenericMessageDto> approveHr(@PathVariable Long id, @Valid @RequestBody ApprovalDtos.FlaApprovalDto data, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        leaveService.approveLeaveByHr(id, data.getIsApproved());
        String returnMessage;
        if(data.getIsApproved()) returnMessage = "Leave application approved";
        else returnMessage = "Leave application rejected";
        return ResponseEntity.ok(new GenericMessageDto(returnMessage));
    }

//    @PutMapping("/approve/{id}")
//    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam String role) {
//        LeaveRequest req = leaveRepo.findById(id).orElseThrow();
//        if (role.equals("FLA")) {
//            req.setCurrentLevel("SLA");
//        } else if (role.equals("SLA")) {
//            req.setCurrentLevel("HR");
//        } else if (role.equals("HR")) {
//            req.setStatus("APPROVED");
//            req.setCurrentLevel("DONE");
//        }
//        leaveRepo.save(req);
//        return ResponseEntity.ok("Approved by " + role);
//    }
//
//    @PutMapping("/reject/{id}")
//    public ResponseEntity<?> reject(@PathVariable Long id, @RequestParam String role) {
//        LeaveRequest req = leaveRepo.findById(id).orElseThrow();
//        req.setStatus("REJECTED");
//        req.setCurrentLevel(role);
//        leaveRepo.save(req);
//        return ResponseEntity.ok("Rejected by " + role);
//    }
}


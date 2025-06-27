package com.example.leave_application.controller;

import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveRequestRepository leaveRepo;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequest request, Authentication auth) {
        request.setEmail(auth.getName()); // logged-in user
        request.setStatus("PENDING");
        request.setCurrentLevel("FLA");
        request.setAppliedAt(LocalDateTime.now());
        leaveRepo.save(request);
        return ResponseEntity.ok("Leave request submitted.");
    }

    @GetMapping("/all")
    public List<LeaveRequest> allRequests() {
        return leaveRepo.findAll();
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam String role) {
        LeaveRequest req = leaveRepo.findById(id).orElseThrow();
        if (role.equals("FLA")) {
            req.setCurrentLevel("SLA");
        } else if (role.equals("SLA")) {
            req.setCurrentLevel("HR");
        } else if (role.equals("HR")) {
            req.setStatus("APPROVED");
            req.setCurrentLevel("DONE");
        }
        leaveRepo.save(req);
        return ResponseEntity.ok("Approved by " + role);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestParam String role) {
        LeaveRequest req = leaveRepo.findById(id).orElseThrow();
        req.setStatus("REJECTED");
        req.setCurrentLevel(role);
        leaveRepo.save(req);
        return ResponseEntity.ok("Rejected by " + role);
    }
}


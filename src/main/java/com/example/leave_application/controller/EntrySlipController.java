package com.example.leave_application.controller;

import com.example.leave_application.model.EntrySlip;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.EntrySlipRepository;
import com.example.leave_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/entry-slip")
public class EntrySlipController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntrySlipRepository slipRepo;

@PostMapping("/apply")
public ResponseEntity<?> applyEntry(
        @RequestBody EntrySlip slip,
        @RequestParam String targetLevel,
        @RequestParam String approverEmail,
        Authentication auth) {

    User user = userRepository.findUserByEmail(auth.getName()).orElseThrow();

    slip.setCreatedBy(user);
    slip.setStatus(targetLevel.equalsIgnoreCase("FLA") ? "PENDING_FLA" : "PENDING_SLA");
    slip.setCurrentLevel(targetLevel.toUpperCase());
    slip.setApproverEmail(approverEmail);
    slip.setAppliedAt(LocalDateTime.now());

    slipRepo.save(slip);
    return ResponseEntity.ok("Entry slip submitted to " + targetLevel);
}





    @GetMapping("/all")
    public List<EntrySlip> all() {
        return slipRepo.findAll();
    }

    //Get all entryslip who are pending for SLA
    @GetMapping("/pending/sla")
    public List<EntrySlip> pendingForSLA(Authentication auth) {
        String email = auth.getName();

        System.err.println("Email of the user: " + email);
        List<EntrySlip> pendingIndent= slipRepo.findByCurrentLevelAndApproverEmailAndStatus("SLA", email, "PENDING_SLA" );
        return ResponseEntity.ok(pendingIndent).getBody();

    }
    @GetMapping("/pending/fla")
    public List<EntrySlip> pendingForFLA(Authentication auth) {
        String email = auth.getName();
        System.err.println("Email of the user in fla api: " + email);
        List<EntrySlip> pendingIndent= slipRepo.findByCurrentLevelAndApproverEmailAndStatus("FLA", email, "PENDING_FLA" );
        return ResponseEntity.ok(pendingIndent).getBody();

    }
    @GetMapping("/pending/hr")
        public List<EntrySlip> pendingForHR(Authentication auth) {
//            String email = auth.getName();

//            System.err.println("Email of the user: " + email);
            System.err.println("asdjfheue heuiheuheuehueheuheuheuheuhuehueehueh: " );

            List<EntrySlip> pendingIndent= slipRepo.findByCurrentLevelAndStatus("HR", "PENDING_HR" );
            return ResponseEntity.ok(pendingIndent).getBody();
        }




    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id,
                                     @RequestParam String role,
                                     @RequestParam(required = false) String nextApproverEmail,
                                     Authentication auth) {
        EntrySlip slip = slipRepo.findById(id).orElseThrow();
        User approver = userRepository.findUserByEmail(auth.getName()).orElseThrow();

        switch (role.toUpperCase()) {
            case "FLA":
                slip.setApprovedByFLA(approver);
                slip.setCurrentLevel("SLA");
                slip.setStatus("PENDING_SLA");

                // Assign selected SLA as next approver
                if (nextApproverEmail != null) {
                    slip.setApproverEmail(nextApproverEmail);
                }
                break;

            case "SLA":
                slip.setApprovedBySLA(approver);
                slip.setCurrentLevel("HR");
                slip.setStatus("PENDING_HR");
                break;

            case "HR":
                slip.setApprovedByHR(approver);
                slip.setCurrentLevel("COMPLETED");
                slip.setStatus("COMPLETED");
                break;
        }

        slipRepo.save(slip);
        return ResponseEntity.ok("Approved by " + role);
    }




    @PutMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestParam String role) {
        EntrySlip slip = slipRepo.findById(id).orElseThrow();
        slip.setStatus("REJECTED");
        slip.setCurrentLevel(role);
        slipRepo.save(slip);
        return ResponseEntity.ok("Rejected by " + role);
    }

    @GetMapping("/user")
    public ResponseEntity<List<EntrySlip>> getUserEntrySlips(Authentication auth) {
        User user = userRepository.findUserByEmail(auth.getName()).orElseThrow();
        List<EntrySlip> slips = slipRepo.findByCreatedBy(user);
        return ResponseEntity.ok(slips);
    }

    @GetMapping("approved")
    public ResponseEntity<List<EntrySlip>> getApprovedEntrySlips(Authentication auth) {
//        User user = userRepository.findUserByEmail(auth.getName()).orElseThrow();
        List<EntrySlip> slips = slipRepo.findByStatus( "COMPLETED");
        return ResponseEntity.ok(slips);
    }

}

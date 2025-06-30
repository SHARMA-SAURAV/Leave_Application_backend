package com.example.leave_application.controller;

import com.example.leave_application.model.EntrySlip;
import com.example.leave_application.repository.EntrySlipRepository;
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
    private EntrySlipRepository slipRepo;

//    @PostMapping("/apply")
//    public ResponseEntity<?> applyEntry(@RequestBody EntrySlip slip,
//                                        @RequestParam String targetLevel, // "FLA" or "SLA"
//                                        Authentication auth) {
//        EntrySlip entrySlip =new EntrySlip();
//        entrySlip.setInTime(slip.getInTime());
////        slip.setInTime();
//        slip.setStatus("PENDING");
//        slip.setCurrentLevel(targetLevel.toUpperCase()); // "FLA" or "SLA"
//        slip.setAppliedAt(LocalDateTime.now());
//        slipRepo.save(slip);
//
//        return ResponseEntity.ok("Entry slip submitted to " + slip.getCurrentLevel());
//    }





@PostMapping("/apply")
public ResponseEntity<?> applyEntry(
        @RequestBody EntrySlip slip,
        @RequestParam String targetLevel,         // "FLA" or "SLA"
        @RequestParam String approverEmail,       // selected approver
        Authentication auth) {

    slip.setEmail(auth.getName());               // the applicant's email
    slip.setStatus("PENDING");
    slip.setCurrentLevel(targetLevel.toUpperCase());
    slip.setApproverEmail(approverEmail);
    slip.setAppliedAt(LocalDateTime.now());

    // Make sure inTime and outTime are parsed correctly
    // If `EntrySlip` uses LocalTime, and frontend sends "HH:mm", it will work

    slipRepo.save(slip);

    return ResponseEntity.ok("Entry slip submitted to " + targetLevel);
}


    @GetMapping("/all")
    public List<EntrySlip> all() {
        return slipRepo.findAll();
    }

//    @PutMapping("/approve/{id}")
//    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam String role) {
//        EntrySlip slip = slipRepo.findById(id).orElseThrow();
//        if (role.equals("FLA")) {
//            slip.setCurrentLevel("SLA");
//        } else if (role.equals("SLA")) {
//            slip.setCurrentLevel("HR");
//        } else if (role.equals("HR")) {
//            slip.setStatus("APPROVED");
//            slip.setCurrentLevel("DONE");
//        }
//        slipRepo.save(slip);
//        return ResponseEntity.ok("Approved by " + role);
//    }




    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam String role) {
        EntrySlip slip = slipRepo.findById(id).orElseThrow();

        switch (role.toUpperCase()) {
            case "FLA":
                slip.setCurrentLevel("SLA");
                break;
            case "SLA":
                if ("FLA".equalsIgnoreCase(slip.getCurrentLevel())) {
                    slip.setCurrentLevel("HR");
                } else {
                    slip.setStatus("APPROVED");
                    slip.setCurrentLevel("DONE");
                }
                break;
            case "HR":
                slip.setStatus("APPROVED");
                slip.setCurrentLevel("DONE");
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
}

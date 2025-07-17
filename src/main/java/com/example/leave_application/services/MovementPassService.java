package com.example.leave_application.services;


import com.example.leave_application.dto.CommonEmailTemplates;
import com.example.leave_application.dto.EmailTemplate;
import com.example.leave_application.model.*;
import com.example.leave_application.repository.MovementPassRepository;
import com.example.leave_application.repository.MovementPassRepository;
import com.example.leave_application.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.example.leave_application.exception.CommonExceptions.validationError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovementPassService {
    @Autowired
    MovementPassRepository movementPassRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public void generateMovementPass(MovementPass data) {
        data.validate();
        movementPassRepository.save(data);
    }

    @Transactional
    public List<MovementPass> getEmployeeMovementPasses(User user) {
        return movementPassRepository.findByRequestedByOrderByAppliedAtDesc(user);
    }

    @Transactional
    public MovementPass getMovementPassById(Long id) {
        return movementPassRepository.findById(id).orElseThrow(
                validationError("Movement pass not found")
        );
    }

    public User getHrForPass(MovementPass movementPass) {
        // Find any user with HR role
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleType.HR))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public List<MovementPass> getFlaRequests() {
        return movementPassRepository.findByStatusOrderByAppliedAtDesc(MovementPassStatus.FLA);
    }

    @Transactional
    public void approvePassByFla(Long movementPassId, Long sla, Boolean isApproved) {
        MovementPass movementPass = getMovementPassById(movementPassId);
        if (!movementPass.getStatus().equals(MovementPassStatus.FLA)) {
            throw new ValidationException("This application doesn't need FLA approval.");
        }
        if (isApproved) {
            if (sla == null) throw new ValidationException("Please specify SLA when accepting request.");
            User slaSelected = userRepository.findById(sla).orElseThrow(
                    validationError("Provided SLA not found")
            );
            if (!slaSelected.getRoles().contains(RoleType.SLA)) {
                throw new ValidationException("Provided person is not SLA");
            }
            movementPass.setStatus(MovementPassStatus.SLA);
            movementPass.setSlaApprover(slaSelected);
        } else movementPass.setStatus(MovementPassStatus.REJECTED);
        movementPass.setFlaApprovalAt(LocalDateTime.now());
        movementPass.validate();
        movementPassRepository.save(movementPass);
        if(!isApproved) {
            CommonEmailTemplates.ApprovedEmailTemplate template = new CommonEmailTemplates.ApprovedEmailTemplate("Movement Pass", "rejected");
            emailService.sendTemplate(movementPass.getRequestedBy().getEmail(), template);
        } else {
            EmailTemplate template = new CommonEmailTemplates.MovementPassApprovalTemplate(movementPass);
            emailService.sendTemplate(movementPass.getSlaApprover().getEmail(), template);
        }
    }

    @Transactional
    public List<MovementPass> getSlaRequests() {
        return movementPassRepository.findByStatusOrderByAppliedAtDesc(MovementPassStatus.SLA);
    }

    @Transactional
    public void approvePassBySla(Long movementPassId, Boolean isApproved) {
        MovementPass movementPass = getMovementPassById(movementPassId);
        if (!movementPass.getStatus().equals(MovementPassStatus.SLA)) {
            throw new ValidationException("This application doesn't need SLA approval.");
        }
        if (isApproved) {
            movementPass.setStatus(MovementPassStatus.HR);
        } else movementPass.setStatus(MovementPassStatus.REJECTED);
        movementPass.setSlaApprovalAt(LocalDateTime.now());
        movementPass.validate();
        movementPassRepository.save(movementPass);
        if(!isApproved) {
            EmailTemplate template = new CommonEmailTemplates.ApprovedEmailTemplate("Movement Pass", "rejected");
            emailService.sendTemplate(movementPass.getRequestedBy().getEmail(), template);
        } else {
            User hr = getHrForPass(movementPass);
            EmailTemplate template = new CommonEmailTemplates.MovementPassApprovalTemplate(movementPass);
            emailService.sendTemplate(hr.getEmail(), template);
        }
    }

    @Transactional
    public List<MovementPass> getHrRequests() {
        return movementPassRepository.findByStatusOrderByAppliedAtDesc(MovementPassStatus.HR);
    }

    @Transactional
    public List<MovementPass> getApprovedRequests() {
        return movementPassRepository.findByStatusOrderByAppliedAtDesc(MovementPassStatus.APPROVED);
    }

    @Transactional
    public void approvePassByHr(Long movementPassId, boolean isApproved) {
        MovementPass movementPass = getMovementPassById(movementPassId);
        if (!movementPass.getStatus().equals(MovementPassStatus.HR)) {
            throw new ValidationException("This application doesn't need HR approval.");
        }
        if (isApproved) movementPass.setStatus(MovementPassStatus.APPROVED);
        else movementPass.setStatus(MovementPassStatus.REJECTED);
        movementPass.setHrApprovalAt(LocalDateTime.now());
        movementPass.validate();
        movementPassRepository.save(movementPass);
        String action = isApproved ? "Approved" : "Rejected";
        CommonEmailTemplates.ApprovedEmailTemplate template = new CommonEmailTemplates.ApprovedEmailTemplate("Movement Pass", action);
        emailService.sendTemplate(movementPass.getRequestedBy().getEmail(), template);
    }
}

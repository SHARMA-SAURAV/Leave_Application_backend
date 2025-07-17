package com.example.leave_application.services;


import com.example.leave_application.dto.CommonEmailTemplates;
import com.example.leave_application.dto.EmailTemplate;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.LeaveStatus;
import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import com.example.leave_application.repository.LeaveRequestRepository;
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
public class LeaveService {
    @Autowired
    LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public void generateLeaveApplication(LeaveRequest data) {
        data.validate();
        leaveRequestRepository.save(data);
    }

    @Transactional
    public List<LeaveRequest> getEmployeeLeaveApplications(User user) {
        return leaveRequestRepository.findByRequestedByOrderByAppliedAtDesc(user);
    }

    @Transactional
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id).orElseThrow(
                validationError("Leave application not found")
        );
    }

    public User getHrForLeave(LeaveRequest leaveRequest) {
        // Find any user with HR role
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleType.HR))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public List<LeaveRequest> getUpcomingLeaves(Long flaId, Long slaId, String searchString) {
        if(searchString == null) searchString = "";
        return leaveRequestRepository.findUpcomingLeaves(flaId, slaId, searchString, LocalDate.now(), LeaveStatus.APPROVED);
    }

    @Transactional
    public List<LeaveRequest> getFlaRequests() {
        return leaveRequestRepository.findByStatusOrderByAppliedAtDesc(LeaveStatus.FLA);
    }

    @Transactional
    public void approveLeaveByFla(Long leaveRequestId, Long sla, String substitute, Boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveRequestId);
        if (!leaveRequest.getStatus().equals(LeaveStatus.FLA)) {
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
            leaveRequest.setStatus(LeaveStatus.SLA);
            leaveRequest.setSlaApprover(slaSelected);
            leaveRequest.setSubstitute(substitute);
        } else {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        }
        leaveRequest.setFlaApprovalAt(LocalDateTime.now());
        leaveRequest.validate();
        leaveRequestRepository.save(leaveRequest);
        if(!isApproved) {
            EmailTemplate template = new CommonEmailTemplates.ApprovedEmailTemplate("Leave Application", "rejected");
            emailService.sendTemplate(leaveRequest.getRequestedBy().getEmail(), template);
        } else {
            EmailTemplate template = new CommonEmailTemplates.LeaveRequestApprovalTemplate(leaveRequest);
            emailService.sendTemplate(leaveRequest.getSlaApprover().getEmail(), template);
        }
    }

    @Transactional
    public List<LeaveRequest> getSlaRequests() {
        return leaveRequestRepository.findByStatusOrderByAppliedAtDesc(LeaveStatus.SLA);
    }

    @Transactional
    public void approveLeaveBySla(Long leaveRequestId, String substitute, Boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveRequestId);
        if (!leaveRequest.getStatus().equals(LeaveStatus.SLA)) {
            throw new ValidationException("This application doesn't need SLA approval.");
        }
        if (isApproved) {
            leaveRequest.setStatus(LeaveStatus.HR);
            if (leaveRequest.getSubstitute() == null) {
                if (substitute == null) throw new ValidationException("Please specify substitute.");
                leaveRequest.setSubstitute(substitute);
            }
        } else {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        }
        leaveRequest.setSlaApprovalAt(LocalDateTime.now());
        leaveRequest.validate();
        leaveRequestRepository.save(leaveRequest);
        if(!isApproved) {
            EmailTemplate template = new CommonEmailTemplates.ApprovedEmailTemplate("Leave Application", "rejected");
            emailService.sendTemplate(leaveRequest.getRequestedBy().getEmail(), template);
        } else {
            User hr = getHrForLeave(leaveRequest);
            EmailTemplate template = new CommonEmailTemplates.LeaveRequestApprovalTemplate(leaveRequest);
            emailService.sendTemplate(hr.getEmail(), template);
        }
    }

    @Transactional
    public List<LeaveRequest> getHrRequests() {
        return leaveRequestRepository.findByStatusOrderByAppliedAtDesc(LeaveStatus.HR);
    }

    @Transactional
    public List<LeaveRequest> getApprovedRequests() {
        return leaveRequestRepository.findByStatusOrderByAppliedAtDesc(LeaveStatus.APPROVED);
    }

    @Transactional
    public void approveLeaveByHr(Long leaveRequestId, boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveRequestId);
        if (!leaveRequest.getStatus().equals(LeaveStatus.HR)) {
            throw new ValidationException("This application doesn't need HR approval.");
        }
        String action;
        if (isApproved) {
            leaveRequest.setStatus(LeaveStatus.APPROVED);
            action = "Approved";
        }   else {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
            action = "Rejected";
        }
        leaveRequest.setHrApprovalAt(LocalDateTime.now());
        leaveRequest.validate();
        leaveRequestRepository.save(leaveRequest);
        EmailTemplate template = new CommonEmailTemplates.ApprovedEmailTemplate("Leave Application", action);
        emailService.sendTemplate(leaveRequest.getRequestedBy().getEmail(), template);
    }

//    public void getAllApprovedLeaves() {
//        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByStatus(LeaveStatus.APPROVED, Sort.by(Sort.Direction.DESC, "appliedAt"));
//        return leaveRequestRepository.findByStatus(LeaveStatus.APPROVED, Sort.by(Sort.Direction.DESC, "appliedAt"));
//    }

}

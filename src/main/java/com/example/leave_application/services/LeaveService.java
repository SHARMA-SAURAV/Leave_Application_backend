package com.example.leave_application.services;


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
import org.springframework.stereotype.Service;

import static com.example.leave_application.exception.CommonExceptions.validationError;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveService {
    @Autowired
    LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void generateLeaveApplication(LeaveRequest data) {
        leaveRequestRepository.save(data);
        data.validate();
    }

    @Transactional
    public List<LeaveRequest> getEmployeeLeaveApplications(User user) {
        return leaveRequestRepository.findByRequestedByOrderByAppliedAtDesc(user);
    }

    @Transactional
    protected LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id).orElseThrow(
                validationError("Leave application not found")
        );
    }

    @Transactional
    public void approveLeaveByFla(Long leaveRequestId, Long sla, Boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveRequestId);
        if(!leaveRequest.getStatus().equals(LeaveStatus.FLA)) {
            throw new ValidationException("This application doesn't need FLA approval.");
        }
        if(isApproved) {
            if(sla == null) throw new ValidationException("Please specify SLA when accepting request.");
            User slaSelected = userRepository.findById(sla).orElseThrow(
                    validationError("Provided SLA not found")
            );
            if(!slaSelected.getRoles().contains(RoleType.SLA)){
                throw new ValidationException("Provided person is not SLA");
            }
            leaveRequest.setStatus(LeaveStatus.SLA);
            leaveRequest.setSlaApprover(slaSelected);
        }
        else leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setFlaApprovalAt(LocalDateTime.now());
        leaveRequest.validate();
        leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public void approveLeaveBySla(Long leaveRequestId, Boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveRequestId);
        if(!leaveRequest.getStatus().equals(LeaveStatus.SLA)) {
            throw new ValidationException("This application doesn't need SLA approval.");
        }
        if(isApproved) leaveRequest.setStatus(LeaveStatus.HR);
        else leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setSlaApprovalAt(LocalDateTime.now());
        leaveRequest.validate();
        leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public void approveLeaveByHr(Long leaveRequestId, boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveRequestId);
        if(!leaveRequest.getStatus().equals(LeaveStatus.HR)) {
            throw new ValidationException("This application doesn't need HR approval.");
        }
        if(isApproved) leaveRequest.setStatus(LeaveStatus.APPROVED);
        else leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setHrApprovalAt(LocalDateTime.now());
        leaveRequest.validate();
        leaveRequestRepository.save(leaveRequest);
    }

//    public void getAllApprovedLeaves() {
//        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByStatus(LeaveStatus.APPROVED, Sort.by(Sort.Direction.DESC, "appliedAt"));
//        return leaveRequestRepository.findByStatus(LeaveStatus.APPROVED, Sort.by(Sort.Direction.DESC, "appliedAt"));
//    }

}

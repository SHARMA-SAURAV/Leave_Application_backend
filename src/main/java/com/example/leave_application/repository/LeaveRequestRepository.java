package com.example.leave_application.repository;

import com.example.leave_application.model.EntrySlip;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.LeaveStatus;
import com.example.leave_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
//    List<LeaveRequest> findByEmail(String email);
//    List<LeaveRequest> findByCurrentLevelAndEmail(String currentLevel, String status);
    List<LeaveRequest> findByRequestedByOrderByAppliedAtDesc(User requestedBy);
    List<LeaveRequest> findByStatusOrderByAppliedAtDesc(LeaveStatus status);

    @Query("""
    SELECT lr FROM LeaveRequest lr JOIN lr.requestedBy rb
        WHERE (:sla_id IS NULL OR :sla_id = lr.slaApprover.id)
            AND (:fla_id IS NULL OR :fla_id = lr.flaApprover.id)
            AND (LOWER(rb.name) LIKE CONCAT('%', LOWER(:name), '%'))
            AND (lr.startDate >= :today)
            AND (lr.status = :status)
            ORDER BY lr.startDate
            LIMIT 200
    """)
    List<LeaveRequest> findUpcomingLeaves(@Param("fla_id") Long flaId, @Param("sla_id") Long slaId, @Param("name") String searchToken, @Param("today") LocalDate today, @Param("status") LeaveStatus status);
}

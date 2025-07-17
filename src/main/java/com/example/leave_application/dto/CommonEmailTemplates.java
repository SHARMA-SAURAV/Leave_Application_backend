package com.example.leave_application.dto;

import com.example.leave_application.model.EntrySlip;
import com.example.leave_application.model.LeaveRequest;
import com.example.leave_application.model.MovementPass;
import lombok.AllArgsConstructor;
import lombok.Data;

public class CommonEmailTemplates {
    static final String greeting = "Dear User,\n\n";
    static final String footer = "\n\nRegards,\nLeave Management System\n";

    @Data
    @AllArgsConstructor
    public static class ApprovedEmailTemplate implements EmailTemplate {
        private String requestType;
        private String action;
        @Override
        public String getSubject() {
            return "Your " + requestType + " has been " + action + ".";
        }
        @Override
        public String getBody() {
            return greeting + "Your " + requestType + " has been successfully " + action + "." + footer;
        }
    }

    @Data
    @AllArgsConstructor
    public static class EntrySlipApprovalTemplate implements EmailTemplate {
        private EntrySlip slip;
        @Override
        public String getSubject() {
            return "A new Entry Slip is awaiting your approval. ";
        }
        @Override
        public String getBody() {
            return greeting +
                "The following Entry Slip is waiting for your approval.\n\n"+

                "Employee Name: " + slip.getCreatedBy().getName() + "\n" +
                "Email: " + slip.getCreatedBy().getEmail() + "\n" +
                "Department: " + slip.getCreatedBy().getDepartment()+ "\n" +
                "Employee ID: " + slip.getCreatedBy().getEmployeeId() + "\n" +
                "Date: " + slip.getDate() + "\n" +
                "In Time: " + slip.getInTime() + "\n" +
                "Out Time: " + slip.getOutTime() + "\n" +
                "Reason: " + slip.getReason() +
                footer;
        }
    }

    @Data
    @AllArgsConstructor
    public static class LeaveRequestApprovalTemplate implements EmailTemplate {
        private LeaveRequest request;
        @Override
        public String getSubject() {
            return "A new Leave Request is awaiting your approval. ";
        }
        @Override
        public String getBody() {
            return greeting +
                "The following Leave Request is waiting for your approval.\n\n"+

                "Employee Name: " + request.getRequestedBy().getName() + "\n" +
                "Email: " + request.getRequestedBy().getEmail() + "\n" +
                "Department: " + request.getRequestedBy().getDepartment()+ "\n" +
                "Employee ID: " + request.getRequestedBy().getEmployeeId() + "\n" +
                "Dates: " + request.getStartDate() + " to " + request.getEndDate() + "\n" +
                "Reason: " + request.getReason() +
                footer;
        }
    }

    @Data
    @AllArgsConstructor
    public static class MovementPassApprovalTemplate implements EmailTemplate {
        private MovementPass pass;
        @Override
        public String getSubject() {
            return "A new Movement Pass is awaiting your approval. ";
        }
        @Override
        public String getBody() {
            return greeting +
                "The following Movement Pass is waiting for your approval.\n\n"+

                "Employee Name: " + pass.getRequestedBy().getName() + "\n" +
                "Email: " + pass.getRequestedBy().getEmail() + "\n" +
                "Department: " + pass.getRequestedBy().getDepartment()+ "\n" +
                "Employee ID: " + pass.getRequestedBy().getEmployeeId() + "\n" +
                "Date: " + pass.getDate() + "\n" +
                "Reason: " + pass.getReason() +
                footer;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ResetPasswordTemplate implements EmailTemplate {
        private String url;
        @Override
        public String getSubject() {
            return "Reset your Password";
        }
        @Override
        public String getBody() {
            return greeting +
                "You can use the following link to reset your password.\n\n"+
                url +
                footer;
        }
    }
}

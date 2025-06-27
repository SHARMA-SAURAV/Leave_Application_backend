package com.example.leave_application.dto;

import com.example.leave_application.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String email;
    private String password;
    private Set<RoleType> roles;
}

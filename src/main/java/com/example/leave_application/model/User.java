package com.example.leave_application.model;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This annotation is used to specify that the field is a primary key and its value will be generated automatically.
    private Long id;
    private String name;
    private String email;
    private String password;
//    private String role; // This field can be used to differentiate between different user roles, such as ADMIN or USER.
    private String department; // This field can be used to specify the department of the user, such as HR, IT, etc.
    private String designation; // This field can be used to specify the designation of the user, such as Manager, Employee, etc.
    private String employeeId; // This field can be used to specify the employee ID of the user, which can be useful for identification purposes.
    private String phoneNumber; // This field can be used to specify the phone number of the user, which can be useful for contact purposes.
    private String sex;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RoleType> roles= new HashSet<>();

}

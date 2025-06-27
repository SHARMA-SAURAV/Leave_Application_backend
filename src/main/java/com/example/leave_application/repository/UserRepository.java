package com.example.leave_application.repository;

import com.example.leave_application.model.RoleType;
import com.example.leave_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") RoleType role);

//    Optional<User> findUserByEmployeeId(String employeeId);
//
//    List<User> findAllByRolesContaining(RoleType roleType);
//
//    List<User> findAllByDepartment(String department);
}

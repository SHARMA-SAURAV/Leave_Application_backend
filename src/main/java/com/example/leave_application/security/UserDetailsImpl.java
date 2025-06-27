package com.example.leave_application.security;

import com.example.leave_application.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {


//    @Autowired
//    private User user;

    private final User user;
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public Long getId() {
        return (long) user.getId();
    }
//    public UserDetailsImpl(com.example.demo.model.User user) {
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // add entiry which return user in this
    // create the method to return the User object
    public User getUser() {
        return user;
    }

    // This method is not part of UserDetails interface



//    public User getUser() {
//        return null;
//    }
}

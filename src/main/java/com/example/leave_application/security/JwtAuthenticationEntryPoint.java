package com.example.leave_application.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.getWriter().write(
                "{ \"error\": \"Unauthorized\", \"message\": \"" +
                        authException.getMessage() +
                        "\" }"
        );
    }

}

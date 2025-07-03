package com.example.leave_application.exception;


import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CommonExceptions {
    public static Supplier<ValidationException> validationError(String message) {
        return () -> new ValidationException(message);
    }
}

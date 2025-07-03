package com.example.leave_application.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        if (ex.getBindingResult().hasErrors()) {
            FieldError firstError = ex.getBindingResult().getFieldError();
            if (firstError != null) {
                String errorMessage = firstError.getField() + ": " + firstError.getDefaultMessage();
                ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", errorMessage);
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", "Validation failed"), HttpStatus.BAD_REQUEST);
    }

    // I am throwing ValidationException as default
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse error = new ErrorResponse("BUSINESS_LOGIC_ERROR", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Catch all other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        System.err.println("Unhandled exception: " + ex.getMessage());
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse("OTHER_ERROR", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

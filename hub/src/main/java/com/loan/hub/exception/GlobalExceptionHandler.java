package com.loan.hub.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.*;

/**
 * Handles validation errors and returns meaningful HTTP 400 responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotated request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleVadidationsExceptions(MethodArgumentNotValidException exception){
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(errors);
}

        /**
         * Handles any unexpected exceptions and returns a generic error message.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleGenericException(Exception exception) {
                return ResponseEntity.internalServerError().body("Internal server error");
        }
        /**
         * Handles invalid enum values in the request body and returns a clear error message.
        */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleInvalidEnum(HttpMessageNotReadableException ex) {

        List<String> errors = new ArrayList<>();

        Throwable cause = ex.getMostSpecificCause();

        if (cause != null && cause.getMessage() != null) {
                String message = cause.getMessage();

                if (message.contains("EmploymentType")) {
                        errors.add("Invalid employmentType. Allowed values: SALARIED, SELF_EMPLOYED");
                }

                if (message.contains("LoanPurpose")) {
                        errors.add("Invalid purpose. Allowed values: PERSONAL, HOME, AUTO");
                }
        }

        if (errors.isEmpty()) {
                errors.add("Invalid request format");
        }    

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }
}



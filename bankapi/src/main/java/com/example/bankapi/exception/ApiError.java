package com.example.bankapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String status;       // e.g. "404 NOT_FOUND"
    private final String errorCode;    // machine-readable code, e.g. "ACCOUNT_NOT_FOUND"
    private final String message;      // human-readable description
    private final Instant timestamp;
    private final List<FieldError> fieldErrors;  // non-null only for 400 validation failures

    public ApiError(String status, String errorCode, String message, List<FieldError> fieldErrors) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.timestamp = Instant.now();
    }

    // Getters -- required for Jackson serialization
    public String getStatus() { return status; }
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
    public List<FieldError> getFieldErrors() { return fieldErrors; }

    // Nested record for per-field validation errors
    public record FieldError(String field, String message) {}
}
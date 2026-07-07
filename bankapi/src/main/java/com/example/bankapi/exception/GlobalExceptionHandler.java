package com.example.bankapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
                "404 NOT_FOUND",
                ex.getResourceType().toUpperCase() + "_NOT_FOUND",
                ex.getMessage(),
                null
        ));
    }

    // Handles BusinessRuleException -- returns 422
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiError(
                "422 UNPROCESSABLE_ENTITY",
                ex.getErrorCode(),
                ex.getMessage(),
                null
        ));
    }

    // Handles MalformedRequestException (missing field, bad JSON, non-positive amount, etc) -- returns 400
    @ExceptionHandler(MalformedRequestException.class)
    public ResponseEntity<ApiError> handleMalformedRequest(MalformedRequestException ex) {
        List<ApiError.FieldError> fieldErrors = null;
        if (ex.getFieldName() != null) {
            fieldErrors = List.of(new ApiError.FieldError(ex.getFieldName(), ex.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                "400 BAD_REQUEST",
                ex.getErrorCode(),
                ex.getMessage(),
                fieldErrors
        ));
    }

    // Handles validation failures from @Valid -- returns 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                "400 BAD_REQUEST",
                "VALIDATION_FAILURE",
                "One or more fields failed validation",
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handles DuplicateIdempotencyKeyException -- returns 409
    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    public ResponseEntity<ApiError> handleDuplicateIdempotencyKey(DuplicateIdempotencyKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(
                "409 CONFLICT",
                "DUPLICATE_IDEMPOTENCY_KEY",
                ex.getMessage(),
                null
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                "500 INTERNAL_SERVER_ERROR",
                "INTERNAL_ERROR",
                "An unexpected error occured. Please contact support.",
                null
        ));
    }
}
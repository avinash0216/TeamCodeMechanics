package com.example.bankapi.exception;

// Thrown when a request is malformed or invalid (missing field, bad JSON, non-positive amount, etc).
// This maps to 400 Bad Request - the request could not be understood.
public class MalformedRequestException extends RuntimeException {

    private final String errorCode;
    private final String fieldName;

    public MalformedRequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = null;
    }

    public MalformedRequestException(String errorCode, String fieldName, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public String getErrorCode() { return errorCode; }
    public String getFieldName() { return fieldName; }
}

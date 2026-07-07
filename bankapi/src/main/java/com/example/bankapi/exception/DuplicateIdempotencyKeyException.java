package com.example.bankapi.exception;

public class DuplicateIdempotencyKeyException extends RuntimeException {

    public DuplicateIdempotencyKeyException(String idempotencyKey) {
        super("Idempotency key already used: " + idempotencyKey);
    }
}

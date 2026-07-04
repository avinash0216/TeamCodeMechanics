package com.example.bankbff.dto;

import java.time.Instant;

/**
 * Generic API response wrapper used for both success and error responses.
 */
public record ApiResponse<T>(
        String status,
        String errorCode,
        String message,
        String timestamp,
        String path,
        T data
) {
    public static <T> ApiResponse<T> success(String status, String message, String path, T data) {
        return new ApiResponse<>(status, null, message, Instant.now().toString(), path, data);
    }

    public static <T> ApiResponse<T> failure(String status, String errorCode, String message, String path) {
        return new ApiResponse<>(status, errorCode, message, Instant.now().toString(), path, null);
    }
}

package com.airbnb.shared.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;
    private Map<String, String> errors;
    private String path;
    private LocalDateTime timestamp;

    // Private constructor for builder
    private ApiResponse(Builder<T> builder) {
        this.success = builder.success;
        this.status = builder.status;
        this.message = builder.message;
        this.data = builder.data;
        this.errors = builder.errors;
        this.path = builder.path;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
    }

    // Getters
    public boolean isSuccess() { return success; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Map<String, String> getErrors() { return errors; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Static builder method
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    // Convenience static factory methods
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(int status, String message, Map<String, String> errors, String path) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static ApiResponse<Void> error(int status, String message, String path) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Builder class
    public static class Builder<T> {
        private boolean success;
        private int status;
        private String message;
        private T data;
        private Map<String, String> errors;
        private String path;
        private LocalDateTime timestamp;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> status(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }

        public Builder<T> path(String path) {
            this.path = path;
            return this;
        }

        public Builder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }
}
package com.example.orderservice.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ujednolicony format bledu zwracanego przez API zamiast surowego stack trace'a.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private List<String> details;

    public ErrorResponse(int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(int status, String message, List<String> details) {
        this(status, message);
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }
}

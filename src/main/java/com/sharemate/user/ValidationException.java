package com.sharemate.user;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
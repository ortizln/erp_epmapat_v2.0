package com.erp.emails.service;

public class EmailBlacklistViolationException extends RuntimeException {
    public EmailBlacklistViolationException(String message) {
        super(message);
    }
}

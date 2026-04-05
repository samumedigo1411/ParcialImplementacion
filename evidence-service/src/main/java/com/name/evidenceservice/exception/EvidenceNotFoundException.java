package com.name.evidenceservice.exception;

public class EvidenceNotFoundException extends RuntimeException {
    public EvidenceNotFoundException(String message) {
        super(message);
    }
}
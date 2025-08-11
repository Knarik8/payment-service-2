package com.iprody.payment.service.app.exception;

import java.time.Instant;
import java.util.UUID;

public record ErrorDto(int errorCode, Instant timestamp, String message, String operation,
                       UUID entityId) {

    public ErrorDto(int errorCode, String message, String operation, UUID entityId) {
        this(errorCode, Instant.now(), message, operation, entityId);
    }

    public ErrorDto(int errorCode, String message) {
        this(errorCode, Instant.now(), message, null, null);
    }
}


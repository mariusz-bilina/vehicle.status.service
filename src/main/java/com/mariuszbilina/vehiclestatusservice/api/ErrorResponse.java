package com.mariuszbilina.vehiclestatusservice.api;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ErrorResponse {
    private final String title;
    private final String details;
    private final int status;

    ErrorResponse(String title, String details, int status) {
        this.title = title;
        this.details = details;
        this.status = status;
    }

    String getTitle() {
        return title;
    }

    String getDetails() {
        return details;
    }

    int getStatus() {
        return status;
    }
}

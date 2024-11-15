package com.mariuszbilina.vehiclestatusservice.api;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

@Serdeable
public record VehicleStatusResponse(
        UUID requestId,
        String vin,
        Boolean accidentFree,
        String maintenanceScore
) {
}

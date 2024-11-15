package com.mariuszbilina.vehiclestatusservice.api;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record VehicleStatusRequest(String vin, List<String> features) {
}

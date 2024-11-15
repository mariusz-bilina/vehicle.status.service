package com.mariuszbilina.vehiclestatusservice.adapters.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record GetMaintenanceFrequencyResponse(@JsonProperty("maintenance_frequency") String maintenanceFrequency) {
}

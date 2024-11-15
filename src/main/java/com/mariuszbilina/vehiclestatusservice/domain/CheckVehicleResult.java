package com.mariuszbilina.vehiclestatusservice.domain;

public record CheckVehicleResult(
        String vin,
        AccidentFreeFeature accidentFreeFeature,
        MaintenanceScoreFeature maintenanceScoreFeature
) {
}

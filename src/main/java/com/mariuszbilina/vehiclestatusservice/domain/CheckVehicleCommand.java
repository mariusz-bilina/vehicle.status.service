package com.mariuszbilina.vehiclestatusservice.domain;

import java.util.List;

public record CheckVehicleCommand(String vin, List<String> features) {

    public static final String ACCIDENT_FREE_FEATURE = "accident_free";
    public static final String MAINTENANCE_FEATURE = "maintenance";

    public CheckVehicleCommand {
        if (vin == null) {
            throw new CheckVehicleValidationException("Vin cannot be null");
        }
        if ("" .equals(vin)) {
            throw new CheckVehicleValidationException("Vin cannot be empty");
        }
        if (features == null) {
            throw new CheckVehicleValidationException("Features list cannot be null");
        }
        if (features.isEmpty()) {
            throw new CheckVehicleValidationException("Features list cannot be empty");
        }
        boolean illegalFeature = features.stream().anyMatch(s -> !MAINTENANCE_FEATURE.equals(s) && !ACCIDENT_FREE_FEATURE.equals(s));
        if (illegalFeature) {
            throw new CheckVehicleValidationException("Features list contains illegal feature");
        }
    }

    public boolean isAccidentFreeFeatureEnabled() {
        return features.stream().anyMatch(ACCIDENT_FREE_FEATURE::equals);
    }

    public boolean isMaintenanceCheckEnabled() {
        return features.stream().anyMatch(MAINTENANCE_FEATURE::equals);
    }
}

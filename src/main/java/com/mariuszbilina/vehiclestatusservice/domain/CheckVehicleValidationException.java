package com.mariuszbilina.vehiclestatusservice.domain;

public class CheckVehicleValidationException extends RuntimeException {

    public CheckVehicleValidationException(String message) {
        super(message);
    }
}

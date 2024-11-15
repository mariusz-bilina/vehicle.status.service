package com.mariuszbilina.vehiclestatusservice.domain.ports.insurance;

public record GetInsuranceClaimsResult(int claims) {

    public boolean isAccidentFree() {
        return claims == 0;
    }
}
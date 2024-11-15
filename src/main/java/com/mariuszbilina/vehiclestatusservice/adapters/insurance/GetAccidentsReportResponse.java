package com.mariuszbilina.vehiclestatusservice.adapters.insurance;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record GetAccidentsReportResponse(Report report) {

    @Serdeable
    public record Report(Integer claims) {
    }

    public int getClaims() {
        return report.claims;
    }
}

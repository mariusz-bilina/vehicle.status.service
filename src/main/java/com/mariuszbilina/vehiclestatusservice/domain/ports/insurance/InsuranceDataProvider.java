package com.mariuszbilina.vehiclestatusservice.domain.ports.insurance;

import reactor.core.publisher.Mono;

public interface InsuranceDataProvider {

    Mono<GetInsuranceClaimsResult> getInsuranceClaims(String vin);
}

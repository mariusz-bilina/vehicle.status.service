package com.mariuszbilina.vehiclestatusservice.adapters.insurance;

import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.GetInsuranceClaimsResult;
import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.InsuranceDataProvider;
import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.InsuranceDataProviderException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class InsuranceDataProviderAdapter implements InsuranceDataProvider {

    private final InsuranceDataProviderClient insuranceDataProviderClient;

    public InsuranceDataProviderAdapter(InsuranceDataProviderClient insuranceDataProviderClient) {
        this.insuranceDataProviderClient = insuranceDataProviderClient;
    }

    @Override
    public Mono<GetInsuranceClaimsResult> getInsuranceClaims(String vin) {
        return insuranceDataProviderClient.getAccidentsReport(vin)
                .map(response -> new GetInsuranceClaimsResult(response.getClaims()))
                .switchIfEmpty(Mono.error(new InsuranceDataProviderException("Data not found")))
                .onErrorMap(HttpClientResponseException.class, e -> new InsuranceDataProviderException(toMessage(e)));
    }

    private static String toMessage(HttpClientResponseException e) {
        return e.getStatus().getCode() + " " + e.getStatus().getReason();
    }
}

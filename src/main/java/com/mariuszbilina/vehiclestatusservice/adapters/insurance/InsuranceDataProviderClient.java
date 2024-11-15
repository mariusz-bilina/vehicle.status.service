package com.mariuszbilina.vehiclestatusservice.adapters.insurance;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import reactor.core.publisher.Mono;

@Client(id = "insurance")
public interface InsuranceDataProviderClient {

    @Get("/accidents/report?vin={vin_number}")
    @Retryable(attempts = "3", delay = "500ms")
    Mono<GetAccidentsReportResponse> getAccidentsReport(@PathVariable("vin_number") String vinNumber);
}

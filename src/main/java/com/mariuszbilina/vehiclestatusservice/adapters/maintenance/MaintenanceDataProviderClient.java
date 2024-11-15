package com.mariuszbilina.vehiclestatusservice.adapters.maintenance;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import reactor.core.publisher.Mono;

@Client(id = "maintenance")
public interface MaintenanceDataProviderClient {

    @Get("/cars/{vin}")
    @Retryable(attempts = "3", delay = "500ms")
    Mono<GetMaintenanceFrequencyResponse> getMaintenanceFrequency(@PathVariable("vin") String vin);
}

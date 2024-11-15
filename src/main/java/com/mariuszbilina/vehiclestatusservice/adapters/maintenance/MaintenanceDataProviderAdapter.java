package com.mariuszbilina.vehiclestatusservice.adapters.maintenance;

import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.GetMaintenanceFrequencyResult;
import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.MaintenanceDataProvider;
import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.MaintenanceDataProviderException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class MaintenanceDataProviderAdapter implements MaintenanceDataProvider {

    private final MaintenanceDataProviderClient maintenanceDataProviderClient;

    public MaintenanceDataProviderAdapter(MaintenanceDataProviderClient maintenanceDataProviderClient) {
        this.maintenanceDataProviderClient = maintenanceDataProviderClient;
    }

    @Override
    public Mono<GetMaintenanceFrequencyResult> getMaintenanceFrequency(String vin) {
        return maintenanceDataProviderClient.getMaintenanceFrequency(vin)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new MaintenanceDataProviderException("Data not found")))
                .onErrorMap(HttpClientResponseException.class, e -> new MaintenanceDataProviderException(toMessage(e)));
    }

    private static String toMessage(HttpClientResponseException e) {
        return e.getStatus().getCode() + " " + e.getStatus().getReason();
    }

    private GetMaintenanceFrequencyResult toResponse(GetMaintenanceFrequencyResponse response) {
        String mappedFrequency = switch (response.maintenanceFrequency()) {
            case "very_low", "low" -> "poor";
            case "medium" -> "average";
            case "high" -> "good";
            default ->
                    throw new MaintenanceDataProviderException("Illegal frequency received: " + response.maintenanceFrequency());
        };
        return new GetMaintenanceFrequencyResult(mappedFrequency);
    }
}

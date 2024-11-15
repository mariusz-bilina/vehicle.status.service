package com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance;

import reactor.core.publisher.Mono;

public interface MaintenanceDataProvider {

    Mono<GetMaintenanceFrequencyResult> getMaintenanceFrequency(String vin);
}

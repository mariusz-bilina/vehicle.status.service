package com.mariuszbilina.vehiclestatusservice.domain;

import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.InsuranceDataProvider;
import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.MaintenanceDataProvider;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Singleton
public class VehicleChecking {

    private final InsuranceDataProvider insuranceDataProvider;
    private final MaintenanceDataProvider maintenanceDataProvider;

    public VehicleChecking(InsuranceDataProvider insuranceDataProvider, MaintenanceDataProvider maintenanceDataProvider) {
        this.insuranceDataProvider = insuranceDataProvider;
        this.maintenanceDataProvider = maintenanceDataProvider;
    }

    public Mono<CheckVehicleResult> checkReactive(CheckVehicleCommand command) {
        Mono<AccidentFreeFeature> accidentFree = checkAccidentFreeIfEnabled(command);
        Mono<MaintenanceScoreFeature> maintenanceLevel = checkMaintenanceLevelIfEnabled(command);
        return Mono.zip(accidentFree, maintenanceLevel)
                .map(tuple -> toCheckVehicleResult(command, tuple));
    }

    private Mono<AccidentFreeFeature> checkAccidentFreeIfEnabled(CheckVehicleCommand command) {
        if (command.isAccidentFreeFeatureEnabled()) {
            return insuranceDataProvider.getInsuranceClaims(command.vin())
                    .map(result -> new AccidentFreeFeature.AccidentFreeFeatureEnabled(result.isAccidentFree()));
        }
        return Mono.just(new AccidentFreeFeature.AccidentFreeFeatureDisabled());
    }

    private Mono<MaintenanceScoreFeature> checkMaintenanceLevelIfEnabled(CheckVehicleCommand command) {
        if (command.isMaintenanceCheckEnabled()) {
            return maintenanceDataProvider.getMaintenanceFrequency(command.vin())
                    .map(result -> new MaintenanceScoreFeature.MaintenanceScoreFeatureEnabled(result.level()));
        }
        return Mono.just(new MaintenanceScoreFeature.MaintenanceScoreFeatureDisabled());
    }

    private static CheckVehicleResult toCheckVehicleResult(CheckVehicleCommand command, Tuple2<AccidentFreeFeature, MaintenanceScoreFeature> tuple) {
        return new CheckVehicleResult(command.vin(), tuple.getT1(), tuple.getT2());
    }
}

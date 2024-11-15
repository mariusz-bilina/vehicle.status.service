package com.mariuszbilina.vehiclestatusservice.domain;

import java.util.Optional;

public sealed interface MaintenanceScoreFeature {

    Optional<String> getResult();

    record MaintenanceScoreFeatureEnabled(String result) implements MaintenanceScoreFeature {

        @Override
        public Optional<String> getResult() {
            return Optional.of(result);
        }
    }

    record MaintenanceScoreFeatureDisabled() implements MaintenanceScoreFeature {

        @Override
        public Optional<String> getResult() {
            return Optional.empty();
        }
    }
}

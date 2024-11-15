package com.mariuszbilina.vehiclestatusservice.domain;

import java.util.Optional;

public sealed interface AccidentFreeFeature {

    Optional<Boolean> getResult();

    record AccidentFreeFeatureEnabled(boolean isAccidentFree) implements AccidentFreeFeature {

        @Override
        public Optional<Boolean> getResult() {
            return Optional.of(isAccidentFree);
        }
    }

    record AccidentFreeFeatureDisabled() implements AccidentFreeFeature {

        @Override
        public Optional<Boolean> getResult() {
            return Optional.empty();
        }
    }
}

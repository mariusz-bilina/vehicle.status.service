package com.mariuszbilina.vehiclestatusservice.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CheckVehicleCommandTest {

    @Test
    void shouldThrowException_whenNullVin() {
        assertThatThrownBy(() -> new CheckVehicleCommand(null, List.of("maintenance"))).isInstanceOf(CheckVehicleValidationException.class);
    }

    @Test
    void shouldThrowException_whenEmptyVin() {
        assertThatThrownBy(() -> new CheckVehicleCommand("", List.of("maintenance"))).isInstanceOf(CheckVehicleValidationException.class);
    }

    @Test
    void shouldThrowException_whenEmptyFeatures() {
        assertThatThrownBy(() -> new CheckVehicleCommand("vin", List.of())).isInstanceOf(CheckVehicleValidationException.class);
    }

    @Test
    void shouldThrowException_whenIllegalFeature() {
        assertThatThrownBy(() -> new CheckVehicleCommand("vin", List.of("illegal_feature"))).isInstanceOf(CheckVehicleValidationException.class);
    }
}
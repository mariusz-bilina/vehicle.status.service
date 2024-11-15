package com.mariuszbilina.vehiclestatusservice.adapters;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.mariuszbilina.vehiclestatusservice.ApiClientTestBase;
import com.mariuszbilina.vehiclestatusservice.adapters.maintenance.MaintenanceDataProviderAdapter;
import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.GetMaintenanceFrequencyResult;
import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.MaintenanceDataProviderException;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Property(name = "micronaut.http.services.maintenance.url", value = "http://localhost:8080")
class MaintenanceDataProviderClientTest extends ApiClientTestBase {

    private static final String VIN = "vin_number";

    @Inject
    MaintenanceDataProviderAdapter maintenanceDataProviderAdapter;

    @Test
    void shouldReturnMappedFrequency_whenCorrectResponseGiven() {
        String responseJson =
                """
                {
                    "maintenance_frequency": "low"
                }
                """;
        wireMock.stubFor(WireMock.get(urlEqualTo("/cars/" + VIN))
                .willReturn(responseWithBody(responseJson)));
        // when
        Mono<GetMaintenanceFrequencyResult> result = maintenanceDataProviderAdapter.getMaintenanceFrequency(VIN);
        // then
        StepVerifier.create(result)
                .expectNext(new GetMaintenanceFrequencyResult("poor"))
                .verifyComplete();
    }

    @Test
    void shouldReturnError_whenUnknownFrequencyReceived() {
        String responseJson =
                """
                {
                    "maintenance_frequency": "unknown"
                }
                """;
        wireMock.stubFor(WireMock.get(urlEqualTo("/cars/" + VIN))
                .willReturn(responseWithBody(responseJson)));
        // when
        Mono<GetMaintenanceFrequencyResult> result = maintenanceDataProviderAdapter.getMaintenanceFrequency(VIN);
        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> assertMaintenanceDataProviderException(throwable, "Illegal frequency received: unknown"))
                .verify();
    }

    @Test
    void shouldReturnError_whenNotFound() {
        // given
        wireMock.stubFor(WireMock.get(urlEqualTo("/cars/" + VIN))
                .willReturn(notFound()));
        // when
        Mono<GetMaintenanceFrequencyResult> result = maintenanceDataProviderAdapter.getMaintenanceFrequency(VIN);
        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> assertMaintenanceDataProviderException(throwable, "Data not found"))
                .verify();
    }

    @Test
    void shouldRetryCallThreeTimes_andReturnError_whenServerStillFails() {
        // given
        wireMock.stubFor(WireMock.get(urlEqualTo("/cars/" + VIN))
                .willReturn(serverError()));
        // when
        Mono<GetMaintenanceFrequencyResult> insuranceClaims = maintenanceDataProviderAdapter.getMaintenanceFrequency(VIN);
        // then
        StepVerifier.create(insuranceClaims)
                .expectErrorMatches(throwable -> assertMaintenanceDataProviderException(throwable, "500 Internal Server Error"))
                .verify();
        wireMock.verify(4, getRequestedFor(urlEqualTo("/cars/" + VIN)));
    }

    @Test
    void shouldReturnError_whenServiceUnavailable() {
        // given
        wireMock.stubFor(WireMock.get(urlEqualTo("/cars/" + VIN))
                .willReturn(serviceUnavailable()));
        // when
        Mono<GetMaintenanceFrequencyResult> insuranceClaims = maintenanceDataProviderAdapter.getMaintenanceFrequency(VIN);
        // then
        StepVerifier.create(insuranceClaims)
                .expectErrorMatches(throwable -> assertMaintenanceDataProviderException(throwable, "503 Service Unavailable"))
                .verify();
    }

    private static boolean assertMaintenanceDataProviderException(Throwable throwable, String message) {
        return throwable instanceof MaintenanceDataProviderException
                && throwable.getMessage().equals(message);
    }
}


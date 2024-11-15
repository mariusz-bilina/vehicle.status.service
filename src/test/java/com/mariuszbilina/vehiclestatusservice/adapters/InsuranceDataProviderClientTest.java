package com.mariuszbilina.vehiclestatusservice.adapters;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.mariuszbilina.vehiclestatusservice.ApiClientTestBase;
import com.mariuszbilina.vehiclestatusservice.adapters.insurance.InsuranceDataProviderAdapter;
import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.GetInsuranceClaimsResult;
import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.InsuranceDataProviderException;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Property(name = "micronaut.http.services.insurance.url", value = "http://localhost:8080")
class InsuranceDataProviderClientTest extends ApiClientTestBase {

    private static final String VIN = "vin_number";

    @Inject
    InsuranceDataProviderAdapter insuranceDataProviderAdapter;

    @Test
    void shouldReturnNumberOfClaims_whenCorrectResponseGiven() {
        String responseJson =
                """
                        {
                            "report": {
                                "claims": 3
                            }
                        }
                        """;
        wireMock.stubFor(WireMock.get(urlEqualTo("/accidents/report?vin=" + VIN))
                .willReturn(responseWithBody(responseJson)));
        // when
        Mono<GetInsuranceClaimsResult> result = insuranceDataProviderAdapter.getInsuranceClaims(VIN);
        // then
        StepVerifier.create(result)
                .expectNext(new GetInsuranceClaimsResult(3))
                .verifyComplete();
    }

    @Test
    void shouldReturnError_whenNotFound() {
        // given
        wireMock.stubFor(WireMock.get(urlEqualTo("/accidents/report?vin=" + VIN))
                .willReturn(notFound()));
        // when
        Mono<GetInsuranceClaimsResult> result = insuranceDataProviderAdapter.getInsuranceClaims(VIN);
        // then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> assertInsuranceDataProviderException(throwable, "Data not found"))
                .verify();
    }

    @Test
    void shouldRetryCallThreeTimes_andReturnError_whenServerStillFails() {
        // given
        wireMock.stubFor(WireMock.get(urlEqualTo("/accidents/report?vin=" + VIN))
                .willReturn(serverError()));
        // when
        Mono<GetInsuranceClaimsResult> insuranceClaims = insuranceDataProviderAdapter.getInsuranceClaims(VIN);
        // then
        StepVerifier.create(insuranceClaims)
                .expectErrorMatches(throwable -> assertInsuranceDataProviderException(throwable, "500 Internal Server Error"))
                .verify();
        wireMock.verify(4, getRequestedFor(urlEqualTo("/accidents/report?vin=" + VIN)));
    }

    @Test
    void shouldReturnError_whenServiceUnavailable() {
        // given
        wireMock.stubFor(WireMock.get(urlEqualTo("/accidents/report?vin=" + VIN))
                .willReturn(serviceUnavailable()));
        // when
        Mono<GetInsuranceClaimsResult> insuranceClaims = insuranceDataProviderAdapter.getInsuranceClaims(VIN);
        // then
        StepVerifier.create(insuranceClaims)
                .expectErrorMatches(throwable -> assertInsuranceDataProviderException(throwable, "503 Service Unavailable"))
                .verify();
    }

    private static boolean assertInsuranceDataProviderException(Throwable throwable, String message) {
        return throwable instanceof InsuranceDataProviderException
                && throwable.getMessage().equals(message);
    }
}


package com.mariuszbilina.vehiclestatusservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.micronaut.http.MediaType;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@MicronautTest
public class ApiClientTestBase {

    protected WireMockServer wireMock;

    @BeforeEach
    void setup() {
        wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMock.start();
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    protected static ResponseDefinitionBuilder responseWithBody(String responseJson) {
        return aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody(responseJson);
    }
}


package com.mariuszbilina.vehiclestatusservice.api;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mariuszbilina.vehiclestatusservice.ApiClientTestBase;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.MediaType;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

class VehicleStatusIntegrationTest extends ApiClientTestBase {

    private Map<String, Object> getProperties() {
        return Map.of(
                "micronaut.http.services.insurance.url", wireMock.baseUrl(),
                "micronaut.http.services.maintenance.url", wireMock.baseUrl()
        );
    }

    private EmbeddedServer server;

    @BeforeEach
    void setup() {
        server = ApplicationContext.run(EmbeddedServer.class, getProperties());
        RestAssured.port = server.getPort();
    }

    @AfterEach
    void tearDown() {
        server.close();
    }

    @Test
    void shouldReturnStatus_whenAccidentFeatureEnabled() {
        // given
        String responseJson = """
                {
                    "report": {
                        "claims": 3
                    }
                }
                """;
        when(getAccidentReport("vin_number"), responseJson);
        //when
        given().contentType(ContentType.JSON)
                .body("""
                        {
                            "vin": "vin_number",
                            "features": ["accident_free"]
                        }
                        """)
                .when()
                .post("/check")
                .then()
                .statusCode(200)
                .body("vin", is("vin_number"))
                .body("accidentFree", is(false))
                .body("maintenanceScore", nullValue());
    }

    @Test
    void shouldReturnStatus_whenMaintenanceFeatureEnabled() {
        // given
        String responseJson = """
                {
                    "maintenance_frequency": "high"
                }
                """;
        when(getMaintenanceScore("vin_number"), responseJson);
        //when
        given().contentType(ContentType.JSON)
                .body("""
                        {
                            "vin": "vin_number",
                            "features": ["maintenance"]
                        }
                        """)
                .when()
                .post("/check")
                .then()
                .statusCode(200)
                .body("vin", is("vin_number"))
                .body("accidentFree", nullValue())
                .body("maintenanceScore", is("good"));
    }

    @Test
    void shouldReturnError_whenMissingVin() {
        //when
        given().contentType(ContentType.JSON)
                .body("""
                        {
                            "features": ["maintenance"]
                        }
                        """)
                .when()
                .post("/check")
                .then()
                .statusCode(400)
                .body("title", is("Bad request"))
                .body("details", is("Vin cannot be null"));
    }

    @Test
    void shouldReturnError_whenIllegalFeatureProvided() {
        //when
        given().contentType(ContentType.JSON)
                .body("""
                        {
                            "vin": "vin_number",
                            "features": ["illegal_feature"]
                        }
                        """)
                .when()
                .post("/check")
                .then()
                .statusCode(400)
                .body("title", is("Bad request"))
                .body("details", is("Features list contains illegal feature"));
    }

    @Test
    void shouldReturnError_whenIllegalFrequencyReturned() {
        // given
        String responseJson = """
                {
                    "maintenance_frequency": "illegal"
                }
                """;
        when(getMaintenanceScore("vin_number"), responseJson);
        //when
        given().contentType(ContentType.JSON)
                .body("""
                        {
                            "vin": "vin_number",
                            "features": ["maintenance"]
                        }
                        """)
                .when()
                .post("/check")
                .then()
                .statusCode(424)
                .body("title", is("Failed maintenance dependency"))
                .body("details", is("Maintenance data provider failed due to: 'Illegal frequency received: illegal'"))
                .body("status", is(424));
    }

    private void when(MappingBuilder builder, String responseJson) {
        wireMock.stubFor(builder.willReturn(responseWithBody(responseJson)));
    }

    private static MappingBuilder getAccidentReport(String vinNumber) {
        return WireMock.get(urlEqualTo("/accidents/report?vin=" + vinNumber));
    }

    private static MappingBuilder getMaintenanceScore(String vinNumber) {
        return WireMock.get(urlEqualTo("/cars/" + vinNumber));
    }

    protected static ResponseDefinitionBuilder responseWithBody(String responseJson) {
        return aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                .withBody(responseJson);
    }
}

package com.mariuszbilina.vehiclestatusservice.api;

import com.mariuszbilina.vehiclestatusservice.domain.CheckVehicleCommand;
import com.mariuszbilina.vehiclestatusservice.domain.CheckVehicleResult;
import com.mariuszbilina.vehiclestatusservice.domain.CheckVehicleValidationException;
import com.mariuszbilina.vehiclestatusservice.domain.VehicleChecking;
import com.mariuszbilina.vehiclestatusservice.domain.ports.insurance.InsuranceDataProviderException;
import com.mariuszbilina.vehiclestatusservice.domain.ports.maintenance.MaintenanceDataProviderException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class VehicleStatusController {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleStatusController.class);

    private final VehicleChecking vehicleChecking;

    VehicleStatusController(VehicleChecking vehicleChecking) {
        this.vehicleChecking = vehicleChecking;
    }

    @Post("/check")
    public Mono<VehicleStatusResponse> checkStatus(@Body VehicleStatusRequest request) {
        UUID requestId = UUID.randomUUID();
        LOG.info("Received check status request: '{}', body: '{}'", requestId, request);
        CheckVehicleCommand command = new CheckVehicleCommand(request.vin(), request.features());
        Mono<CheckVehicleResult> checkVehicleResultMono = vehicleChecking.checkReactive(command);
        return checkVehicleResultMono.map(checkVehicleResult -> toVehicleStatusResponse(checkVehicleResult, requestId))
                .doOnError(throwable -> LOG.error("Request '{}' failed: '{}'", requestId, throwable.getMessage()))
                .doOnSuccess(vehicleStatusResponse -> LOG.info("Request with id: '{}' processed successfully", vehicleStatusResponse.requestId()));
    }

    private static VehicleStatusResponse toVehicleStatusResponse(CheckVehicleResult result, UUID requestId) {
        String vin = result.vin();
        Boolean accidentFree = result.accidentFreeFeature().getResult().orElse(null);
        String maintenanceScore = result.maintenanceScoreFeature().getResult().orElse(null);
        return new VehicleStatusResponse(requestId, vin, accidentFree, maintenanceScore);
    }

    @Error(exception = CheckVehicleValidationException.class)
    public HttpResponse<ErrorResponse> handle(CheckVehicleValidationException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Bad request",
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.getCode()
        );
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Error(exception = InsuranceDataProviderException.class)
    public HttpResponse<ErrorResponse> handle(InsuranceDataProviderException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Failed insurance dependency",
                String.format("Insurance data provider failed due to: '%s'", exception.getMessage()),
                HttpStatus.FAILED_DEPENDENCY.getCode()
        );
        return HttpResponse.status(HttpStatus.FAILED_DEPENDENCY).body(errorResponse);
    }

    @Error(exception = MaintenanceDataProviderException.class)
    public HttpResponse<ErrorResponse> handle(MaintenanceDataProviderException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Failed maintenance dependency",
                String.format("Maintenance data provider failed due to: '%s'", exception.getMessage()),
                HttpStatus.FAILED_DEPENDENCY.getCode()
        );
        return HttpResponse.status(HttpStatus.FAILED_DEPENDENCY).body(errorResponse);
    }

}

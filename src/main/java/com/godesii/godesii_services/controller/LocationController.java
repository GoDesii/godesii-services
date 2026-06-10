package com.godesii.godesii_services.controller;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.config.LocationConfig;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.LocationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that exposes the configured service location.
 * <p>
 * The latitude, longitude, and radius are read from the active Spring profile's
 * YAML properties (e.g. {@code application-dev.yaml}) under the key
 * {@code app.location.*}.
 * </p>
 *
 * <pre>
 * GET /api/location  →  { latitude, longitude, radius }
 * </pre>
 */
@RestController
@RequestMapping(LocationController.ENDPOINT)
public class LocationController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/location";

    private final LocationConfig locationConfig;

    public LocationController(LocationConfig locationConfig) {
        this.locationConfig = locationConfig;
    }

    /**
     * Returns the configured latitude, longitude, and radius.
     *
     * @return {@link APIResponse} wrapping a {@link LocationResponse}
     */
    @GetMapping
    public ResponseEntity<APIResponse<LocationResponse>> getLocation() {
        LocationResponse locationResponse = new LocationResponse(
                locationConfig.getLatitude(),
                locationConfig.getLongitude(),
                locationConfig.getRadius()
        );

        return ResponseEntity.ok(
                new APIResponse<>(
                        HttpStatus.OK,
                        locationResponse,
                        GoDesiiConstant.SUCCESSFULLY_FETCHED
                )
        );
    }
}

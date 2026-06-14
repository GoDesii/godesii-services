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

import java.util.List;

/**
 * REST Controller that exposes the configured service locations.
 * <p>
 * The locations are loaded from an external JSON file whose classpath
 * is defined in the active Spring profile's YAML properties under
 * {@code app.location.file}.
 * </p>
 *
 * <pre>
 * GET /api/location  →  [ { latitude, longitude, radius }, ... ]
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
     * Returns all configured service-zone locations.
     *
     * @return {@link APIResponse} wrapping a list of {@link LocationResponse}
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<LocationResponse>>> getLocations() {
        List<LocationResponse> locations = locationConfig.getLocations();

        return ResponseEntity.ok(
                new APIResponse<>(
                        HttpStatus.OK,
                        locations,
                        GoDesiiConstant.SUCCESSFULLY_FETCHED
                )
        );
    }
}

package com.godesii.godesii_services.config;

import com.godesii.godesii_services.dto.LocationResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Configuration properties for service locations (geofence centres + radii).
 * <p>
 * Reads a JSON file whose classpath location is specified by
 * {@code app.location.file} in the active YAML profile.
 * The JSON file contains an array of {@link LocationResponse} objects,
 * each representing a service zone with its own latitude, longitude, and radius.
 * </p>
 *
 * <p>Example YAML:</p>
 * <pre>
 * app:
 *   location:
 *     file: classpath:locations-dev.json
 * </pre>
 *
 * <p>Example JSON ({@code locations-dev.json}):</p>
 * <pre>
 * [
 *   { "latitude": 28.6139, "longitude": 77.2090, "radius": 10.0 },
 *   { "latitude": 19.0760, "longitude": 72.8777, "radius": 15.0 }
 * ]
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "app.location")
public class LocationConfig {

    private static final Logger log = LoggerFactory.getLogger(LocationConfig.class);

    private final ResourceLoader resourceLoader;
    private final Gson gson;

    /** Classpath path to the locations JSON file (e.g. {@code classpath:locations-dev.json}). */
    private String file;

    /** Parsed list of service-zone locations. */
    private List<LocationResponse> locations = Collections.emptyList();

    public LocationConfig(ResourceLoader resourceLoader, Gson gson) {
        this.resourceLoader = resourceLoader;
        this.gson = gson;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Loads and parses the locations JSON file after property binding is complete.
     */
    @PostConstruct
    public void loadLocations() {
        if (file == null || file.isBlank()) {
            log.warn("app.location.file is not configured — no service locations loaded");
            return;
        }

        try (InputStream is = resourceLoader.getResource(file).getInputStream();
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            Type listType = new TypeToken<List<LocationResponse>>() {}.getType();
            locations = gson.fromJson(reader, listType);

            if (locations == null) {
                locations = Collections.emptyList();
            }

            log.info("Loaded {} service location(s) from {}", locations.size(), file);

        } catch (Exception e) {
            log.error("Failed to load locations from {}: {}", file, e.getMessage(), e);
            locations = Collections.emptyList();
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Returns the list of configured service-zone locations.
     *
     * @return unmodifiable list of {@link LocationResponse} entries
     */
    public List<LocationResponse> getLocations() {
        return Collections.unmodifiableList(locations);
    }
}

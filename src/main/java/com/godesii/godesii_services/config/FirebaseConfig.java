package com.godesii.godesii_services.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Initializes the Firebase Admin SDK using the service-account credentials
 * bundled in {@code src/main/resources/villsyn-ac142-firebase-adminsdk-fbsvc-43cc3d2ade.json}.
 *
 * <p>Firebase is used for:
 * <ul>
 *   <li><b>FCM (Cloud Messaging)</b> — push notifications to the delivery-partner
 *       mobile app and the customer app.</li>
 * </ul>
 *
 * <p>The SDK is initialised once at startup. Subsequent beans can inject
 * {@link com.google.firebase.messaging.FirebaseMessaging} directly.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    /** Path to the service-account JSON inside the classpath (src/main/resources). */
    private static final String SERVICE_ACCOUNT_PATH =
            "villsyn-ac142-firebase-adminsdk-fbsvc-43cc3d2ade.json";

    @PostConstruct
    public void initializeFirebase() {
        // Guard: only initialise once (relevant during hot-reload / tests)
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("Firebase already initialised — skipping.");
            return;
        }

        try (InputStream serviceAccount =
                     new ClassPathResource(SERVICE_ACCOUNT_PATH).getInputStream()) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialised successfully (project: villsyn-ac142)");

        } catch (IOException e) {
            log.error("Failed to initialise Firebase Admin SDK: {}", e.getMessage(), e);
            throw new IllegalStateException(
                    "Cannot start application — Firebase service-account JSON not found or invalid: "
                            + SERVICE_ACCOUNT_PATH, e);
        }
    }
}

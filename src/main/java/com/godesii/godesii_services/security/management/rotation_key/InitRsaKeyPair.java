package com.godesii.godesii_services.security.management.rotation_key;

import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class InitRsaKeyPair implements ApplicationRunner {

    private final RSAKeysService rsaKeysService;
    private final Keys keys;

    public InitRsaKeyPair(RSAKeysService rsaKeysService, Keys keys) {
        this.rsaKeysService = rsaKeysService;
        this.keys = keys;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(this.rsaKeysService.getAll().isEmpty()) {
            JpaRSAKeysRepository.RsaKeyPair rsaKeys = this.keys.generateKeyPair(Instant.now());
            this.rsaKeysService.saveRSAKeys(rsaKeys);
        }
    }
}

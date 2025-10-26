package com.godesii.godesii_services.security.management.roationkey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InitRSAKeyPair  implements ApplicationRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(InitRSAKeyPair.class);

    private final RSAKeyPairRepository repository;
    private final Keys keys;

    public InitRSAKeyPair(RSAKeyPairRepository repository, Keys keys) {
        this.repository = repository;
        this.keys = keys;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("OAuth2 Runner Init.....");

        if (this.repository.findKeyPairs().isEmpty()) {
            RSAKeyPairRepository.RsaKeyPair keypair = keys.generateKeyPair(Instant.now());
            this.repository.save(keypair);
        }
    }
}

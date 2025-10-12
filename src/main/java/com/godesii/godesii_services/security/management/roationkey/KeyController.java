package com.godesii.godesii_services.security.management.roationkey;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static com.godesii.godesii_services.security.management.roationkey.RSAKeyPairRepository.*;

@RestController
public class KeyController {


    private final RSAKeyPairRepository repository;
    private final Keys keys;

    public KeyController(RSAKeyPairRepository repository, Keys keys) {
        this.repository = repository;
        this.keys = keys;
    }

    @PostMapping("/oauth2/new_jwks")
    public String generate() {
        RsaKeyPair keypair = keys.generateKeyPair(Instant.now());
        this.repository.save(keypair);
        return keypair.id();
    }

}

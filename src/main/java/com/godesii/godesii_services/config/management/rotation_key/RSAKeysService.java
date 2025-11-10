package com.godesii.godesii_services.config.management.rotation_key;

import com.godesii.godesii_services.entity.auth.RSAKeys;
import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository;
import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository.RsaKeyPair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RSAKeysService {

    private final JpaRSAKeysRepository jpaRSAKeysRepository;
    private final RSAPrivateKeyConverter rsaPrivateKeyConverter;
    private final RSAPublicKeyConverter rsaPublicKeyConverter;

    public RSAKeysService(JpaRSAKeysRepository jpaRSAKeysRepository, RSAPrivateKeyConverter rsaPrivateKeyConverter, RSAPublicKeyConverter rsaPublicKeyConverter) {
        this.jpaRSAKeysRepository = jpaRSAKeysRepository;
        this.rsaPrivateKeyConverter = rsaPrivateKeyConverter;
        this.rsaPublicKeyConverter = rsaPublicKeyConverter;
    }

    public void saveRSAKeys(RsaKeyPair rsaKeys) {
        try (ByteArrayOutputStream privateBaos = new ByteArrayOutputStream(); var publicBaos = new ByteArrayOutputStream()) {
            RSAKeys keys = new RSAKeys();
            this.rsaPrivateKeyConverter.serialize(rsaKeys.privateKey(), privateBaos);
            this.rsaPublicKeyConverter.serialize(rsaKeys.publicKey(), publicBaos);
            keys.setCreatedAt(rsaKeys.created());
            keys.setPrivateKey(privateBaos.toString());
            keys.setPublicKey(publicBaos.toString());
            this.jpaRSAKeysRepository.save(keys);
        }
        catch (IOException ex){

        }
    }

    public List<RSAKeys> getAll() {
        return this.jpaRSAKeysRepository.findAll();
    }
}

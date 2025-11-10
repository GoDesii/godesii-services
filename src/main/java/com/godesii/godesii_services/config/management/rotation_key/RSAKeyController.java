package com.godesii.godesii_services.config.management.rotation_key;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping(RSAKeyController.ENDPOINT)
public class RSAKeyController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/keys/jwks";

    private final Keys keys;
    private final RSAKeysService rsaKeysService;

    public RSAKeyController(Keys keys, RSAKeysService rsaKeysService) {
        this.keys = keys;
        this.rsaKeysService = rsaKeysService;
    }

    @PostMapping
    public ResponseEntity<Object> generateRSAKey(){
        JpaRSAKeysRepository.RsaKeyPair rsaKeys = this.keys.generateKeyPair(Instant.now());
        this.rsaKeysService.saveRSAKeys(rsaKeys);
        APIResponse<Object> apiResponse = new APIResponse<>(HttpStatus.CREATED, "Successfully Created!");
        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }
}

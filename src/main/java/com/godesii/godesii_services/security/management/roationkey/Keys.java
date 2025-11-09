//package com.godesii.godesii_services.security.management.roationkey;
//
//import com.godesii.godesii_services.security.management.roationkey.RSAKeyPairRepository.RsaKeyPair;
//import org.springframework.stereotype.Component;
//
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.RSAKeyGenParameterSpec;
//import java.time.Instant;
//import java.util.UUID;
//
//
//@Component
//public class Keys {
//
//    RsaKeyPair generateKeyPair(Instant created) {
//        KeyPair keyPair = generateRsaKey();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        return new RsaKeyPair(UUID.randomUUID().toString(), created, publicKey, privateKey);
//    }
//
//    private KeyPair generateRsaKey() {
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            return keyPairGenerator.generateKeyPair();
//        }//
//        catch (Exception ex) {
//            throw new IllegalStateException(ex);
//        }
//    }
//
//    private KeyPair generateRsaKeyWithProvider(String provider){
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", provider);
//            keyPairGenerator.initialize(new RSAKeyGenParameterSpec(2048,RSAKeyGenParameterSpec.F4));
//            return keyPairGenerator.generateKeyPair();
//        }catch (Exception ex){
//            throw new IllegalStateException(ex);
//        }
//    }
//}

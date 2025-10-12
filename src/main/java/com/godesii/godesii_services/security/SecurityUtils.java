package com.godesii.godesii_services.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityUtils {

    public static String generateOTP() {
        StringBuilder generatedOTP = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        try{
            secureRandom = SecureRandom.getInstance(secureRandom.getAlgorithm());
            for(int i=0;i<6;i++){
                generatedOTP.append(secureRandom.nextInt(9));
            }
        }
        catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
        return generatedOTP.toString();
    }

}

package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.entity.auth.SecureToken;
import com.godesii.godesii_services.repository.auth.JpaSecureTokenRepository;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class SecureTokenService{

        private final JpaSecureTokenRepository secureTokenRepository;

        public SecureTokenService(JpaSecureTokenRepository secureTokenRepository) {
            this.secureTokenRepository = secureTokenRepository;
        }

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


        public void savesecureToken(SecureToken secureToken) {
            this.secureTokenRepository.save(secureToken);
        }

        public SecureToken findByOTP(String secureToken) {
            return null;
        }

        public void removeOTP(SecureToken secureToken) {

        }

        public Boolean isExpired(SecureToken secureToken) {
            return null;
        }
}

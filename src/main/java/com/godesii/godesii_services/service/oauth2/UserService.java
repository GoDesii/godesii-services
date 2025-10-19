package com.godesii.godesii_services.service.oauth2;

import com.godesii.godesii_services.config.twilio.SmsRequest;
import com.godesii.godesii_services.config.twilio.TwilioSmsSenderService;
import com.godesii.godesii_services.dto.UserCreationRequest;
import com.godesii.godesii_services.entity.oauth2.SecureToken;
import com.godesii.godesii_services.entity.oauth2.User;
import com.godesii.godesii_services.repository.oauth2.UserRepository;
import com.godesii.godesii_services.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService  {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private static final String MOBILE_NUMBER_REGEX = "^\\d{10}$"; // Example: 10 digits


    private final UserRepository userRepository;
    private final SecureTokenService secureTokenService;
    private final TwilioSmsSenderService twilioSmsSenderService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, SecureTokenService secureTokenService, TwilioSmsSenderService twilioSmsSenderService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.secureTokenService = secureTokenService;
        this.twilioSmsSenderService = twilioSmsSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserCreationRequest request){
        this.userRepository.findByUsername("");
        return null;
    }

    public User registerMobileUser(String mobileNo){
        if(!isMobileNumber(mobileNo)) {
            throw new IllegalArgumentException("");
        }

        String generatedOTP = SecurityUtils.generateOTP();
        LOGGER.info("Generated OTP " + generatedOTP);
        this.twilioSmsSenderService.sendSms(new SmsRequest(mobileNo, "OTP Send! " + generatedOTP));
        User existingUser = checkIfUserExistByMobileNo(mobileNo);
        if(existingUser != null){
            existingUser.setLoginOtp(passwordEncoder.encode(generatedOTP));
            existingUser.setAccountNonExpired(true);
            existingUser.setAccountNonLocked(true);
            existingUser.setCredentialsNonExpired(true);
            return userRepository.save(existingUser);
        }
        User user = new User();
        user.setMobileNo(mobileNo);
        user.setMobileNoVerified(false);
        user.setLoginOtp(passwordEncoder.encode(generatedOTP));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        return this.userRepository.save(user);
    }

    public User checkIfUserExist(String username){
        return this.userRepository.findByUsername(username).orElse(null);
    }

    public User checkIfUserExistByMobileNo(String mobileNo){
        return this.userRepository.findByMobileNo(mobileNo).orElse(null);
    }

    // You can refine this regex for specific country codes, optional prefixes, etc.

    public static boolean isMobileNumber(String username) {
        Pattern pattern = Pattern.compile(MOBILE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean isString(String username) {
        return !isMobileNumber(username);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Hey");
        return null;
    }
}

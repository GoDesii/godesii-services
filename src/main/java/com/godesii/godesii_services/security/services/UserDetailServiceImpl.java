package com.godesii.godesii_services.security.services;

import com.godesii.godesii_services.entity.oauth2.User;
import com.godesii.godesii_services.repository.oauth2.JpaSecureTokenRepository;
import com.godesii.godesii_services.repository.oauth2.UserRepository;
import com.godesii.godesii_services.security.UserPrincipal;
import com.godesii.godesii_services.service.oauth2.SecureTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    private JpaSecureTokenRepository tokenRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User userByUsername =  userRepository.findByUsername(identifier).orElse(null);
        if(userByUsername != null){
            return null;
        }

        User userByMobileNo = userRepository.findByMobileNo(identifier).orElse(null);

        if(userByMobileNo != null){
            return UserPrincipal.buildWithId(userByMobileNo.getId().toString())
                    .username(userByMobileNo.getMobileNo())
                    .password(userByMobileNo.getLoginOtp())
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();
        }

        throw new UsernameNotFoundException("User not found with identifier:" + identifier);
    }

}

package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.security.UserPrincipal;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

public record UserDetailServiceImpl(UserRepository userRepository) implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User userByUsername = this.userRepository.findByUsername(identifier).orElse(null);
        if (userByUsername != null) {
            return buildUserPrincipal(userByUsername);
        }

        User userByMobileNo = this.userRepository.findByMobileNo(identifier).orElse(null);
        if (userByMobileNo != null) {
            return buildUserPrincipal(userByMobileNo);
        }

        throw new UsernameNotFoundException("User not found with identifier:" + identifier);
    }

    private static UserPrincipal buildUserPrincipal(User user) {

        return UserPrincipal.buildWithId(user.getId().toString())
                .username(user.getMobileNo())
                .password(user.getLoginOtp())
                .roles(Set.of(user.getRole().name()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

}

package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.entity.auth.Role;
import com.godesii.godesii_services.security.UserPrincipal;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

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

        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<String> roles = new HashSet<>(user.getRoles().size());
        if(!user.getRoles().isEmpty()) {
            user.getRoles().forEach(userRole -> {
                roles.add(userRole.name());
                authorities.add(new SimpleGrantedAuthority(userRole.name()));
                userRole.getPermissions().forEach(permission ->
                        authorities.add(new SimpleGrantedAuthority(permission.name()))
                );
            });
        }

        return UserPrincipal.buildWithId(user.getId().toString())
                .username(user.getMobileNo())
                .password(user.getLoginOtp())
                .roles(roles)
                .authorities(authorities)
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .enabled(user.getEnabled())
                .build();
    }

}

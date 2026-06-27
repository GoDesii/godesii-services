package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.entity.auth.Role;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import com.godesii.godesii_services.security.UserPrincipal;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

public record UserDetailServiceImpl(UserRepository userRepository, RestaurantRepo restaurantRepo)
        implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User userByUsername = this.userRepository.findByUsername(identifier).orElse(null);
        if (userByUsername != null) {
            return buildUserPrincipal(userByUsername, restaurantRepo);
        }

        User userByMobileNo = this.userRepository.findByMobileNo(identifier).orElse(null);
        if (userByMobileNo != null) {
            return buildUserPrincipal(userByMobileNo, restaurantRepo);
        }

        throw new UsernameNotFoundException("User not found with identifier:" + identifier);
    }

    private static UserPrincipal buildUserPrincipal(User user, RestaurantRepo restaurantRepo) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<String> roles = new HashSet<>(user.getRoles().size());
        if (!user.getRoles().isEmpty()) {
            user.getRoles().forEach(userRole -> {
                roles.add(userRole.name());
                authorities.add(new SimpleGrantedAuthority(userRole.name()));
                userRole.getPermissions()
                        .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.name())));
            });
        }

        // Resolve restaurantId if the user is a restaurant owner (VENDOR)
        Long restaurantId = null;
        if (user.getRoles().contains(Role.VENDOR)) {
            restaurantId = restaurantRepo
                    .findAllByCreatedBy(user.getId().toString(), PageRequest.of(0, 1))
                    .stream()
                    .map(Restaurant::getId)
                    .findFirst()
                    .orElse(null);
        }

        return UserPrincipal.buildWithId(user.getId().toString())
                .username(user.getMobileNo())
                .password(user.getLoginOtp())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .countryCode(user.getCountryCode())
                .gender(user.getGender() != null ? user.getGender().getValue() : "")
                .isMobileVerified(user.getMobileNoVerified())
                .roles(roles)
                .enabled(true)
                .authorities(authorities)
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .restaurantId(restaurantId)
                // .enabled(user.getEnabled())
                .build();
    }

    public static UserPrincipal buildRestrictedPrincipal(UserPrincipal principal, String requestedRole) {
        java.util.Set<String> restrictedRoles = new java.util.HashSet<>();
        restrictedRoles.add(requestedRole);

        java.util.List<org.springframework.security.core.GrantedAuthority> restrictedAuthorities = new java.util.ArrayList<>();
        restrictedAuthorities
                .add(new org.springframework.security.core.authority.SimpleGrantedAuthority(requestedRole));

        try {
            com.godesii.godesii_services.entity.auth.Role roleEnum = com.godesii.godesii_services.entity.auth.Role
                    .valueOf(requestedRole);
            for (com.godesii.godesii_services.entity.auth.Permission perm : roleEnum.getPermissions()) {
                restrictedAuthorities
                        .add(new org.springframework.security.core.authority.SimpleGrantedAuthority(perm.name()));
            }
        } catch (IllegalArgumentException e) {
            // Ignore if role is not found in enum
        }

        return new UserPrincipal.Builder(principal)
                .roles(restrictedRoles)
                .authorities(restrictedAuthorities)
                .build();
    }

}

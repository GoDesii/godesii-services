package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.entity.auth.Gender;
import com.godesii.godesii_services.entity.auth.Role;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import com.godesii.godesii_services.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailServiceImpl userDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailService = new UserDetailServiceImpl(userRepository);
    }

    private User buildUser(String username, String mobile, Set<Role> roles, Gender gender) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setMobileNo(mobile);
        user.setLoginOtp("encodedOtp");
        user.setFirstName("John");
        user.setMiddleName("M");
        user.setLastName("Doe");
        user.setCountryCode("91");
        user.setGender(gender);
        user.setMobileNoVerified(true);
        user.setRoles(roles);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        return user;
    }

    @Test
    void loadByUsername_foundByUsername_shouldReturnUserPrincipal() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        User user = buildUser("john", "9876543210", roles, Gender.MALE);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername("john");

        assertNotNull(result);
        assertInstanceOf(UserPrincipal.class, result);
        assertEquals("9876543210", result.getUsername()); // mobile is set as username in principal
        verify(userRepository).findByUsername("john");
        verify(userRepository, never()).findByMobileNo(anyString());
    }

    @Test
    void loadByUsername_notFoundByUsername_foundByMobile_shouldReturnUserPrincipal() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        User user = buildUser("9876543210", "9876543210", roles, Gender.FEMALE);

        when(userRepository.findByUsername("9876543210")).thenReturn(Optional.empty());
        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername("9876543210");

        assertNotNull(result);
        verify(userRepository).findByUsername("9876543210");
        verify(userRepository).findByMobileNo("9876543210");
    }

    @Test
    void loadByUsername_notFoundAnywhere_shouldThrowUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(userRepository.findByMobileNo("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername("unknown"));
    }

    @Test
    void loadByUsername_multipleRoles_shouldIncludeAllAuthorities() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        roles.add(Role.DELIVERY_PERSON);
        User user = buildUser("multi", "9876543210", roles, Gender.OTHER);

        when(userRepository.findByUsername("multi")).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername("multi");

        assertNotNull(result);
        // Should have role authorities + permission authorities
        assertFalse(result.getAuthorities().isEmpty());
    }

    @Test
    void loadByUsername_noRoles_shouldReturnEmptyAuthorities() {
        Set<Role> roles = new HashSet<>();
        User user = buildUser("noroles", "9876543210", roles, null);

        when(userRepository.findByUsername("noroles")).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername("noroles");

        assertNotNull(result);
        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadByUsername_nullGender_shouldHandleGracefully() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        User user = buildUser("nogender", "9876543210", roles, null);

        when(userRepository.findByUsername("nogender")).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername("nogender");

        assertNotNull(result);
        // gender should be empty string when null
        UserPrincipal principal = (UserPrincipal) result;
        assertEquals("", principal.getGender());
    }
}

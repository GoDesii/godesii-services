package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.config.twilio.TwilioSmsSenderService;
import com.godesii.godesii_services.dto.MobileUserCreationRequest;
import com.godesii.godesii_services.dto.UserProfileCreateRequest;
import com.godesii.godesii_services.dto.UserProfileCreateResponse;
import com.godesii.godesii_services.entity.auth.Gender;
import com.godesii.godesii_services.entity.auth.Role;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecureTokenService secureTokenService;

    @Mock
    private TwilioSmsSenderService twilioSmsSenderService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── isMobileNumber / isString ────────────────────────────────────────────

    @Test
    void isMobileNumber_validTenDigits_shouldReturnTrue() {
        assertTrue(UserService.isMobileNumber("9876543210"));
    }

    @Test
    void isMobileNumber_lessThanTenDigits_shouldReturnFalse() {
        assertFalse(UserService.isMobileNumber("12345"));
    }

    @Test
    void isMobileNumber_moreThanTenDigits_shouldReturnFalse() {
        assertFalse(UserService.isMobileNumber("12345678901"));
    }

    @Test
    void isMobileNumber_withLetters_shouldReturnFalse() {
        assertFalse(UserService.isMobileNumber("abcdefghij"));
    }

    @Test
    void isMobileNumber_withSpecialChars_shouldReturnFalse() {
        assertFalse(UserService.isMobileNumber("+919876543"));
    }

    @Test
    void isMobileNumber_emptyString_shouldReturnFalse() {
        assertFalse(UserService.isMobileNumber(""));
    }

    @Test
    void isString_mobileNumber_shouldReturnFalse() {
        assertFalse(UserService.isString("9876543210"));
    }

    @Test
    void isString_textInput_shouldReturnTrue() {
        assertTrue(UserService.isString("john_doe"));
    }

    // ── registerMobileUser ───────────────────────────────────────────────────

    @Test
    void registerMobileUser_invalidMobile_shouldThrowIllegalArgument() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("abc");
        request.setRoleType("CUSTOMER");

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerMobileUser(request));
    }

    @Test
    void registerMobileUser_newUser_shouldCreateAndReturnOTP() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");
        request.setRoleType("CUSTOMER");

        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-otp");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(twilioSmsSenderService.sendSms(any())).thenReturn(null);

        String otp = userService.registerMobileUser(request);

        assertNotNull(otp);
        assertEquals(6, otp.length()); // OTP is 6 digits
        verify(userRepository).save(any(User.class));
        verify(twilioSmsSenderService).sendSms(any());
    }

    @Test
    void registerMobileUser_existingUser_sameRole_shouldReturnOTP() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");
        request.setRoleType("CUSTOMER");

        User existingUser = new User();
        existingUser.setMobileNo("9876543210");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        existingUser.setRoles(roles);

        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-otp");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(existingUser);

        String otp = userService.registerMobileUser(request);

        assertNotNull(otp);
        verify(userRepository).saveAndFlush(existingUser);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerMobileUser_existingUser_differentRole_shouldAddRole() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");
        request.setRoleType("DELIVERY_PERSON");

        User existingUser = new User();
        existingUser.setMobileNo("9876543210");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        existingUser.setRoles(roles);

        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-otp");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(existingUser);

        String otp = userService.registerMobileUser(request);

        assertNotNull(otp);
        assertTrue(existingUser.getRoles().contains(Role.DELIVERY_PERSON));
        verify(userRepository).saveAndFlush(existingUser);
    }

    @Test
    void registerMobileUser_adminRole_shouldThrowInsufficientAuth() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");
        request.setRoleType("ADMIN");

        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("enc");

        assertThrows(InsufficientAuthenticationException.class,
                () -> userService.registerMobileUser(request));
    }

    @Test
    void registerMobileUser_managerRole_shouldThrowInsufficientAuth() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");
        request.setRoleType("MANGER");

        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("enc");

        assertThrows(InsufficientAuthenticationException.class,
                () -> userService.registerMobileUser(request));
    }

    // ── checkIfUserExist / checkIfUserExistByMobileNo ────────────────────────

    @Test
    void checkIfUserExist_found_shouldReturnUser() {
        User user = new User();
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.checkIfUserExist("john");
        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    void checkIfUserExist_notFound_shouldReturnNull() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertNull(userService.checkIfUserExist("unknown"));
    }

    @Test
    void checkIfUserExistByMobileNo_found_shouldReturnUser() {
        User user = new User();
        user.setMobileNo("9876543210");
        when(userRepository.findByMobileNo("9876543210")).thenReturn(Optional.of(user));

        User result = userService.checkIfUserExistByMobileNo("9876543210");
        assertNotNull(result);
    }

    @Test
    void checkIfUserExistByMobileNo_notFound_shouldReturnNull() {
        when(userRepository.findByMobileNo("0000000000")).thenReturn(Optional.empty());
        assertNull(userService.checkIfUserExistByMobileNo("0000000000"));
    }

    // ── updateProfile ────────────────────────────────────────────────────────

    @Test
    void updateProfile_allFields_shouldUpdateAll() {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setUserId("john");
        request.setFirstName("John");
        request.setMiddleName("M");
        request.setLastName("Doe");
        request.setGender("MALE");

        User existingUser = new User();
        existingUser.setUsername("john");
        existingUser.setGender(Gender.MALE);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setGender(Gender.MALE);
            return u;
        });

        UserProfileCreateResponse response = userService.updateProfile(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("M", response.getMiddleName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void updateProfile_partialFields_shouldUpdateOnlyProvided() {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setUserId("jane");
        request.setFirstName("Jane");
        // middleName, lastName, gender are null/empty

        User existingUser = new User();
        existingUser.setUsername("jane");
        existingUser.setFirstName("OldFirst");
        existingUser.setMiddleName("OldMiddle");
        existingUser.setLastName("OldLast");
        existingUser.setGender(Gender.FEMALE);

        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileCreateResponse response = userService.updateProfile(request);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        // Middle name remains old value because request.getMiddleName() is null
        assertEquals("OldMiddle", response.getMiddleName());
    }

    @Test
    void updateProfile_userNotFound_shouldThrowResourceNotFound() {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setUserId("unknown");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateProfile(request));
    }

    @Test
    void updateProfile_genderUpdate_shouldSetGender() {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setUserId("user1");
        request.setGender("FEMALE");

        User existingUser = new User();
        existingUser.setUsername("user1");
        existingUser.setGender(Gender.MALE);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileCreateResponse response = userService.updateProfile(request);

        assertNotNull(response);
        assertEquals("female", response.getGender());
    }

    // ── createUser ───────────────────────────────────────────────────────────

    @Test
    void createUser_shouldReturnNull() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");

        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        User result = userService.createUser(request);
        assertNull(result);
    }
}

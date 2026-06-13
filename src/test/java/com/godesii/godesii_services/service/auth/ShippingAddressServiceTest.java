package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.ShippingAddressRequest;
import com.godesii.godesii_services.entity.auth.ShippingAddress;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.auth.ShippingAddressRepository;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShippingAddressServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShippingAddressRepository addressRepository;

    @InjectMocks
    private ShippingAddressService shippingAddressService;

    private ShippingAddress sampleAddress;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleAddress = new ShippingAddress();
        sampleAddress.setId(1L);
        sampleAddress.setStreet("MG Road");
        sampleAddress.setHouseNumber("42");
        sampleAddress.setCity("Bangalore");
        sampleAddress.setState("Karnataka");
        sampleAddress.setPinCode("560001");
        sampleAddress.setCountry("India");
        sampleAddress.setLatitude("12.9716");
        sampleAddress.setLongitude("77.5946");
        sampleAddress.setAddressType("HOME");
    }

    // ── saveOrUpdateShippingAddress — create (id <= 0) ───────────────────────

    @Test
    void saveOrUpdate_newAddress_shouldCreateAndReturn() {
        ShippingAddressRequest request = buildRequest(0L);

        when(addressRepository.save(any(ShippingAddress.class))).thenReturn(sampleAddress);

        ShippingAddress result = shippingAddressService.saveOrUpdateShippingAddress(request);

        assertNotNull(result);
        assertEquals("MG Road", result.getStreet());
        verify(addressRepository).save(any(ShippingAddress.class));
        verify(addressRepository, never()).findById(anyLong());
    }

    // ── saveOrUpdateShippingAddress — update (id > 0) ────────────────────────

    @Test
    void saveOrUpdate_existingAddress_allFieldsPopulated_shouldUpdateAll() {
        ShippingAddressRequest request = buildRequest(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(ShippingAddress.class))).thenReturn(sampleAddress);

        ShippingAddress result = shippingAddressService.saveOrUpdateShippingAddress(request);

        assertNotNull(result);
        verify(addressRepository).findById(1L);
        verify(addressRepository).save(sampleAddress);
    }

    @Test
    void saveOrUpdate_existingAddress_partialFields_shouldUpdateOnlyNonBlank() {
        ShippingAddressRequest request = new ShippingAddressRequest();
        request.setId(1L);
        request.setCity("Mumbai");
        // All other fields are null — should not be updated

        ShippingAddress existing = new ShippingAddress();
        existing.setId(1L);
        existing.setCity("Bangalore");
        existing.setStreet("Old Street");

        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(addressRepository.save(any(ShippingAddress.class))).thenAnswer(inv -> inv.getArgument(0));

        ShippingAddress result = shippingAddressService.saveOrUpdateShippingAddress(request);

        assertEquals("Mumbai", result.getCity());
        assertEquals("Old Street", result.getStreet()); // unchanged
    }

    @Test
    void saveOrUpdate_existingAddress_emptyStringFields_shouldNotOverwrite() {
        ShippingAddressRequest request = new ShippingAddressRequest();
        request.setId(1L);
        request.setStreet("");
        request.setCity("");
        request.setState("");
        request.setPinCode("");
        request.setCountry("");
        request.setLatitude("");
        request.setLongitude("");
        request.setHouseNumber("");
        request.setAddressType("");

        ShippingAddress existing = new ShippingAddress();
        existing.setId(1L);
        existing.setStreet("Keep This");
        existing.setCity("Keep This City");

        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(addressRepository.save(any(ShippingAddress.class))).thenAnswer(inv -> inv.getArgument(0));

        ShippingAddress result = shippingAddressService.saveOrUpdateShippingAddress(request);

        assertEquals("Keep This", result.getStreet());
        assertEquals("Keep This City", result.getCity());
    }

    @Test
    void saveOrUpdate_existingAddress_notFound_shouldThrowResourceNotFound() {
        ShippingAddressRequest request = buildRequest(999L);

        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shippingAddressService.saveOrUpdateShippingAddress(request));
    }

    // ── getShippingAddressById ────────────────────────────────────────────────

    @Test
    void getById_found_shouldReturnAddress() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(sampleAddress));

        ShippingAddress result = shippingAddressService.getShippingAddressById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_notFound_shouldThrowResourceNotFound() {
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shippingAddressService.getShippingAddressById(999L));
    }

    // ── getAllShippingAddresses ───────────────────────────────────────────────

    @Test
    void getAll_withResults_shouldReturnList() {
        when(addressRepository.findAllByCreatedBy("john"))
                .thenReturn(Optional.of(List.of(sampleAddress)));

        List<ShippingAddress> result = shippingAddressService.getAllShippingAddresses("john");

        assertEquals(1, result.size());
    }

    @Test
    void getAll_noResults_shouldReturnEmptyList() {
        when(addressRepository.findAllByCreatedBy("unknown"))
                .thenReturn(Optional.empty());

        List<ShippingAddress> result = shippingAddressService.getAllShippingAddresses("unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── deleteShippingAddress ─────────────────────────────────────────────────

    @Test
    void delete_found_shouldDeleteSuccessfully() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(sampleAddress));
        doNothing().when(addressRepository).delete(sampleAddress);

        shippingAddressService.deleteShippingAddress(1L);

        verify(addressRepository).delete(sampleAddress);
    }

    @Test
    void delete_notFound_shouldThrowResourceNotFound() {
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shippingAddressService.deleteShippingAddress(999L));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private ShippingAddressRequest buildRequest(Long id) {
        ShippingAddressRequest request = new ShippingAddressRequest();
        request.setId(id);
        request.setStreet("MG Road");
        request.setHouseNumber("42");
        request.setCity("Bangalore");
        request.setState("Karnataka");
        request.setPinCode("560001");
        request.setCountry("India");
        request.setLatitude("12.9716");
        request.setLongitude("77.5946");
        request.setAddressType("HOME");
        return request;
    }
}

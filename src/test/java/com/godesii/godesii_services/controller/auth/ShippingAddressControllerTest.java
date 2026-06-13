package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.dto.ShippingAddressRequest;
import com.godesii.godesii_services.dto.ShippingAddressResponse;
import com.godesii.godesii_services.entity.auth.ShippingAddress;
import com.godesii.godesii_services.service.auth.ShippingAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShippingAddressControllerTest {

    @Mock
    private ShippingAddressService shippingAddressService;

    @InjectMocks
    private ShippingAddressController controller;

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

    // ── createShippingAddress ────────────────────────────────────────────────

    @Test
    void create_shouldReturn201WithResponse() {
        ShippingAddressRequest request = new ShippingAddressRequest();
        request.setId(0L);
        request.setCity("Bangalore");

        when(shippingAddressService.saveOrUpdateShippingAddress(request)).thenReturn(sampleAddress);

        ResponseEntity<APIResponse<ShippingAddressResponse>> response =
                controller.createShippingAddress(request);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals("Bangalore", response.getBody().getData().getCity());
        assertEquals("Successfully Created", response.getBody().getMessage());
    }

    // ── updateShippingAddress ────────────────────────────────────────────────

    @Test
    void update_shouldReturn200WithResponse() {
        ShippingAddressRequest request = new ShippingAddressRequest();
        request.setId(1L);
        request.setCity("Mumbai");

        ShippingAddress updatedAddress = new ShippingAddress();
        updatedAddress.setId(1L);
        updatedAddress.setCity("Mumbai");

        when(shippingAddressService.saveOrUpdateShippingAddress(request)).thenReturn(updatedAddress);

        ResponseEntity<APIResponse<ShippingAddressResponse>> response =
                controller.updateShippingAddress(request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Mumbai", response.getBody().getData().getCity());
        assertEquals("Successfully Updated", response.getBody().getMessage());
    }

    // ── getShippingAddressById ───────────────────────────────────────────────

    @Test
    void getById_shouldReturn200WithResponse() {
        when(shippingAddressService.getShippingAddressById(1L)).thenReturn(sampleAddress);

        ResponseEntity<APIResponse<ShippingAddressResponse>> response =
                controller.getShippingAddressById(1L);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals("Successfully Fetched", response.getBody().getMessage());
    }

    // ── getAllShippingAddresses ───────────────────────────────────────────────

    @Test
    void getAll_shouldReturn200WithList() {
        when(shippingAddressService.getAllShippingAddresses("john"))
                .thenReturn(List.of(sampleAddress));

        ResponseEntity<APIResponse<List<ShippingAddressResponse>>> response =
                controller.getAllShippingAddresses("john");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("Successfully Fetched", response.getBody().getMessage());
    }

    @Test
    void getAll_empty_shouldReturn200WithEmptyList() {
        when(shippingAddressService.getAllShippingAddresses("nobody"))
                .thenReturn(List.of());

        ResponseEntity<APIResponse<List<ShippingAddressResponse>>> response =
                controller.getAllShippingAddresses("nobody");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getData().isEmpty());
    }

    // ── deleteShippingAddress ────────────────────────────────────────────────

    @Test
    void delete_shouldReturn200() {
        doNothing().when(shippingAddressService).deleteShippingAddress(1L);

        ResponseEntity<APIResponse<Void>> response = controller.deleteShippingAddress(1L);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getData());
        assertEquals("Successfully Deleted", response.getBody().getMessage());
        verify(shippingAddressService).deleteShippingAddress(1L);
    }
}

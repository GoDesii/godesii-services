package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.ShippingAddressRequest;
import com.godesii.godesii_services.entity.auth.ShippingAddress;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.auth.ShippingAddressRepository;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class ShippingAddressService {

    private final UserRepository userRepository;
    private final ShippingAddressRepository addressRepository;

    public ShippingAddressService(UserRepository userRepository,
                                  ShippingAddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }


    public ShippingAddress saveOrUpdateShippingAddress(ShippingAddressRequest request) {
        if (request.getId() > 0) {
            ShippingAddress existingAddress = this.addressRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(""));
            updateShippingAddress(existingAddress, request);
            return this.addressRepository.save(existingAddress);
        }
        ShippingAddress shippingAddress = ShippingAddressRequest.mapToEntity(request);
        return this.addressRepository.save(shippingAddress);

    }

    private void updateShippingAddress(ShippingAddress shippingAddress, ShippingAddressRequest request) {

        if (StringUtils.hasText(request.getStreet()))
            shippingAddress.setStreet(request.getStreet());

        if (StringUtils.hasText(request.getHouseNumber()))
            shippingAddress.setHouseNumber(request.getHouseNumber());

        if (StringUtils.hasText(request.getLatitude()))
            shippingAddress.setLatitude(request.getLatitude());

        if (StringUtils.hasText(request.getLongitude()))
            shippingAddress.setLongitude(request.getLongitude());

        if (StringUtils.hasText(request.getCity()))
            shippingAddress.setCity(request.getCity());

        if (StringUtils.hasText(request.getState()))
            shippingAddress.setState(request.getState());

        if (StringUtils.hasText(request.getPinCode()))
            shippingAddress.setPinCode(request.getPinCode());

        if (StringUtils.hasText(request.getCountry()))
            shippingAddress.setCountry(request.getCountry());

        if (StringUtils.hasText(request.getAddressType()))
            shippingAddress.setAddressType(request.getAddressType());

    }

    public ShippingAddress getShippingAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping Address not found with id: " + id));
    }

    public List<ShippingAddress> getAllShippingAddresses(String username) {
        return addressRepository.findAllByCreatedBy(username).orElse(Collections.emptyList());
    }

    public void deleteShippingAddress(Long id) {
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping Address not found with id: " + id));
        addressRepository.delete(address);
    }
}


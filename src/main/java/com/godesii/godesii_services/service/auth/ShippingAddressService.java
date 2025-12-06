package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.ShippingAddressCreateRequest;
import com.godesii.godesii_services.dto.ShippingAddressCreateResponse;
import com.godesii.godesii_services.entity.auth.ShippingAddress;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.auth.ShippingAddressRepository;
import com.godesii.godesii_services.repository.auth.UserRepository;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShippingAddressService {

    private final UserRepository userRepository;
    private final ShippingAddressRepository addressRepository;

    public ShippingAddressService(UserRepository userRepository,
                                  ShippingAddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }


    public ShippingAddress saveNewShippingAddress(ShippingAddressCreateRequest request, Long userId){
        Optional<User> exitingUser = this.userRepository.findById(userId);
        if(exitingUser.isEmpty())
            throw new ResourceNotFoundException("User does not exist with user id" + userId);

        ShippingAddress shippingAddress = ShippingAddressCreateRequest.mapToEntity(request);
        shippingAddress.setUserId(userId);
        return this.addressRepository.save(shippingAddress);

    }


}

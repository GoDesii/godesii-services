package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.ShippingAddressCreateRequest;
import com.godesii.godesii_services.dto.ShippingAddressCreateResponse;
import com.godesii.godesii_services.entity.auth.ShippingAddress;
import com.godesii.godesii_services.service.auth.ShippingAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ShippingAddressController.ENDPOINT)
public class ShippingAddressController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/shipping/address";

    private final ShippingAddressService shippingAddressService;

    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    @PostMapping(
            value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse<ShippingAddressCreateResponse>> createShippingAddress(@RequestBody ShippingAddressCreateRequest request,
                                                                               @PathVariable(name = "userId") Long userId){

        ShippingAddress shippingAddress = this.shippingAddressService.saveNewShippingAddress(request, userId);
        APIResponse<ShippingAddressCreateResponse> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                ShippingAddressCreateResponse.mapToUserAddressCreateResponse(shippingAddress),
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }

}

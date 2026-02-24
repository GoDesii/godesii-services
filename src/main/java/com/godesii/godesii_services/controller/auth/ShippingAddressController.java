package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.ShippingAddressRequest;
import com.godesii.godesii_services.dto.ShippingAddressResponse;
import com.godesii.godesii_services.entity.auth.ShippingAddress;
import com.godesii.godesii_services.service.auth.ShippingAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<ShippingAddressResponse>> createShippingAddress(@RequestBody ShippingAddressRequest request){

        ShippingAddress shippingAddress = this.shippingAddressService.saveOrUpdateShippingAddress(request);
        APIResponse<ShippingAddressResponse> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                ShippingAddressResponse.mapToUserAddressCreateResponse(shippingAddress),
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }

    @PutMapping("/edit")
    public ResponseEntity<APIResponse<ShippingAddressResponse>> updateShippingAddress(@RequestBody ShippingAddressRequest request) {
        ShippingAddress shippingAddress = this.shippingAddressService.saveOrUpdateShippingAddress(request);
        APIResponse<ShippingAddressResponse> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                ShippingAddressResponse.mapToUserAddressCreateResponse(shippingAddress),
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );
        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ShippingAddressResponse>> getShippingAddressById(@PathVariable Long id) {
        ShippingAddress shippingAddress = shippingAddressService.getShippingAddressById(id);
        APIResponse<ShippingAddressResponse> apiResponse = new APIResponse<>(HttpStatus.OK, ShippingAddressResponse.mapToUserAddressCreateResponse(shippingAddress), GoDesiiConstant.SUCCESSFULLY_FETCHED);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<APIResponse<List<ShippingAddressResponse>>> getAllShippingAddresses(
            @RequestParam(name = "username") String username
    ) {
        List<ShippingAddress> addresses = shippingAddressService.getAllShippingAddresses(username);
        List<ShippingAddressResponse> responseList = addresses.stream().map(ShippingAddressResponse::mapToUserAddressCreateResponse).collect(Collectors.toList());
        APIResponse<List<ShippingAddressResponse>> apiResponse = new APIResponse<>(HttpStatus.OK, responseList, GoDesiiConstant.SUCCESSFULLY_FETCHED);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteShippingAddress(@PathVariable Long id) {
        shippingAddressService.deleteShippingAddress(id);
        APIResponse<Void> apiResponse = new APIResponse<>(HttpStatus.OK, null, GoDesiiConstant.SUCCESSFULLY_DELETED);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

}

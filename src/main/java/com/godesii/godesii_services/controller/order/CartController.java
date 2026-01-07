package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.CartRequest;
import com.godesii.godesii_services.entity.order.Cart;
import com.godesii.godesii_services.service.order.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CartController.ENDPOINT)
public class CartController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/cart";

    private APIResponse<Cart> apiResponse;

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<APIResponse<Cart>> createCart(@RequestBody CartRequest request){
        apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                this.cartService.addItemsToCart(request),
                GoDesiiConstant.SUCCESSFULLY_CREATED);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

}

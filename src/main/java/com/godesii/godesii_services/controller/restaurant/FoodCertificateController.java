//package com.godesii.godesii_services.controller.restaurant;
//
//import com.godesii.godesii_services.common.APIResponse;
//import com.godesii.godesii_services.constant.GoDesiiConstant;
//import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
//import com.godesii.godesii_services.service.restaurant.FoodCertificateService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(FoodCertificateController.ENDPOINT)
//@Tag(name = "Food Certificate API", description = "Manage food certificates for restaurants")
//public class FoodCertificateController {
//
//    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/certificates";
//
//    private final FoodCertificateService service;
//
//    public FoodCertificateController(FoodCertificateService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    @Operation(summary = "Create a new food certificate")
//    public ResponseEntity<APIResponse<FoodCertificate>> create(@RequestBody FoodCertificate cert) {
//        FoodCertificate created = service.create(cert);
//
//        APIResponse<FoodCertificate> apiResponse = new APIResponse<>(
//                HttpStatus.CREATED,
//                created,
//                GoDesiiConstant.SUCCESSFULLY_CREATED
//        );
//
//        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all food certificates")
//    public ResponseEntity<APIResponse<List<FoodCertificate>>> getAll() {
//        List<FoodCertificate> certificates = service.getAll();
//
//        APIResponse<List<FoodCertificate>> apiResponse = new APIResponse<>(
//                HttpStatus.OK,
//                certificates,
//                GoDesiiConstant.SUCCESSFULLY_FETCHED
//        );
//
//        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get a certificate by ID")
//    public ResponseEntity<APIResponse<FoodCertificate>> getById(@PathVariable Long id) {
//        FoodCertificate cert = service.getById(id);
//
//        if (cert == null) {
//            APIResponse<FoodCertificate> apiResponse = new APIResponse<>(
//                    HttpStatus.NOT_FOUND,
//                    null,
//                    GoDesiiConstant.NOT_FOUND
//            );
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }
//
//        APIResponse<FoodCertificate> apiResponse = new APIResponse<>(
//                HttpStatus.OK,
//                cert,
//                GoDesiiConstant.SUCCESSFULLY_FETCHED
//        );
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update an existing certificate")
//    public ResponseEntity<APIResponse<FoodCertificate>> update(@PathVariable Long id, @RequestBody FoodCertificate cert) {
//        FoodCertificate updated = service.update(id, cert);
//
//        if (updated == null) {
//            APIResponse<FoodCertificate> apiResponse = new APIResponse<>(
//                    HttpStatus.NOT_FOUND,
//                    null,
//                    GoDesiiConstant.NOT_FOUND
//            );
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }
//
//        APIResponse<FoodCertificate> apiResponse = new APIResponse<>(
//                HttpStatus.OK,
//                updated,
//                GoDesiiConstant.SUCCESSFULLY_UPDATED
//        );
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete a certificate by ID")
//    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {
//        service.delete(id);
//
//        APIResponse<Void> apiResponse = new APIResponse<>(
//                HttpStatus.NO_CONTENT,
//                null,
//                GoDesiiConstant.SUCCESSFULLY_DELETED
//        );
//
//        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
//    }
//}

//package com.godesii.godesii_services.controller.restaurant;
//
//import com.godesii.godesii_services.common.APIResponse;
//import com.godesii.godesii_services.constant.GoDesiiConstant;
//import com.godesii.godesii_services.entity.restaurant.Review;
//import com.godesii.godesii_services.service.restaurant.ReviewService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(ReviewController.ENDPOINT)
//@Tag(name = "Review API", description = "Manage restaurant reviews")
//public class ReviewController {
//
//    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/reviews";
//    private final ReviewService service;
//
//    public ReviewController(ReviewService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    @Operation(summary = "Create a new review")
//    public ResponseEntity<APIResponse<Review>> create(@RequestBody Review review) {
//        Review created = service.create(review);
//
//        APIResponse<Review> apiResponse = new APIResponse<>(
//                HttpStatus.CREATED,
//                created,
//                GoDesiiConstant.SUCCESSFULLY_CREATED
//        );
//
//        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all reviews")
//    public ResponseEntity<APIResponse<List<Review>>> getAll() {
//        List<Review> reviews = service.getAll();
//
//        APIResponse<List<Review>> apiResponse = new APIResponse<>(
//                HttpStatus.OK,
//                reviews,
//                GoDesiiConstant.SUCCESSFULLY_FETCHED
//        );
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get review by ID")
//    public ResponseEntity<APIResponse<Review>> getById(@PathVariable Long id) {
//        Review review = service.getById(id);
//
//        if (review == null) {
//            APIResponse<Review> apiResponse = new APIResponse<>(
//                    HttpStatus.NOT_FOUND,
//                    null,
//                    GoDesiiConstant.NOT_FOUND
//            );
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }
//
//        APIResponse<Review> apiResponse = new APIResponse<>(
//                HttpStatus.OK,
//                review,
//                GoDesiiConstant.SUCCESSFULLY_FETCHED
//        );
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update a review")
//    public ResponseEntity<APIResponse<Review>> update(@PathVariable Long id, @RequestBody Review review) {
//        Review updated = service.update(id, review);
//
//        if (updated == null) {
//            APIResponse<Review> apiResponse = new APIResponse<>(
//                    HttpStatus.NOT_FOUND,
//                    null,
//                    GoDesiiConstant.NOT_FOUND
//            );
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
//        }
//
//        APIResponse<Review> apiResponse = new APIResponse<>(
//                HttpStatus.OK,
//                updated,
//                GoDesiiConstant.SUCCESSFULLY_UPDATED
//        );
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete a review")
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

package com.godesii.godesii_services.controller;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.service.AwsBucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for AWS S3 Bucket operations
 * Handles file upload, deletion, and listing operations
 */
@RestController
@RequestMapping(AwsBucketController.ENDPOINT)
public class AwsBucketController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/aws/s3";

    private final AwsBucketService awsBucketService;

    public AwsBucketController(AwsBucketService awsBucketService) {
        this.awsBucketService = awsBucketService;
    }

    /**
     * Upload a file to S3 bucket
     * 
     * @param file the file to upload
     * @return ResponseEntity with the file URL
     */
    @PostMapping("/upload")
    public ResponseEntity<APIResponse<String>> uploadFile(@RequestPart("file") MultipartFile file) {
        String fileUrl = awsBucketService.uploadFileAndGetUrl(file);
        return ResponseEntity.ok(
                new APIResponse<>(
                        HttpStatus.CREATED,
                        fileUrl,
                        GoDesiiConstant.SUCCESSFULLY_CREATED
                )
        );
    }

}

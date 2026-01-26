package com.godesii.godesii_services.controller;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.config.S3Service;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(AwsBucketController.ENDPOINT)
public class AwsBucketController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION  + "/aws/s3";

    private final S3Service s3Service;

    @Value("${app.aws.s3.region}")
    private String region;

    @Value("${app.aws.s3.bucketName}")
    private String bucketName;

    public AwsBucketController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<APIResponse<String>> upload(@RequestPart("file") MultipartFile file){
        s3Service.uploadFile(file.getOriginalFilename(), file);
        String responseURL = "https://" +
                bucketName+
                ".s3."+
                region+
                ".amazonaws.com/"+
                UUID.randomUUID() +
                file.getOriginalFilename();
        return ResponseEntity
                .ok(new APIResponse<>(
                        HttpStatus.CREATED,
                        responseURL,
                        GoDesiiConstant.SUCCESSFULLY_CREATED
                ));
    }
}

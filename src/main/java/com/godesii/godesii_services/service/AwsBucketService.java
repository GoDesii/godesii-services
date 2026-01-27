package com.godesii.godesii_services.service;

import com.godesii.godesii_services.config.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service class for AWS S3 Bucket operations
 * Handles business logic for file uploads and URL generation
 */
@Service
public class AwsBucketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsBucketService.class);

    private final S3Service s3Service;

    @Value("${app.aws.s3.region}")
    private String region;

    @Value("${app.aws.s3.bucketName}")
    private String bucketName;

    public AwsBucketService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Uploads a file to S3 bucket and returns the accessible URL
     * 
     * @param file the multipart file to upload
     * @return the public URL of the uploaded file
     */
    public String uploadFileAndGetUrl(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File must have a valid filename");
        }

        // Generate unique filename to avoid conflicts
        String uniqueFilename = generateUniqueFilename(originalFilename);
        
        LOGGER.info("Uploading file: {} as {}", originalFilename, uniqueFilename);
        
        // Upload file to S3
        s3Service.uploadFile(uniqueFilename, file);
        
        // Generate and return the public URL
        String fileUrl = generateFileUrl(uniqueFilename);
        
        LOGGER.info("File uploaded successfully. URL: {}", fileUrl);
        
        return fileUrl;
    }

    /**
     * Generates a unique filename by prepending UUID to original filename
     * 
     * @param originalFilename the original filename
     * @return unique filename with UUID prefix
     */
    private String generateUniqueFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "-" + originalFilename;
    }

    /**
     * Generates the public S3 URL for a given filename
     * 
     * @param filename the filename in S3 bucket
     * @return the complete S3 URL
     */
    private String generateFileUrl(String filename) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", 
                           bucketName, 
                           region, 
                           filename);
    }
}

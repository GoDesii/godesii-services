package com.godesii.godesii_services.config;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service{

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private final AmazonS3 s3client;

    @Value("${app.aws.s3.bucketName}")
    private String bucketName;

    public S3Service(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public ByteArrayOutputStream downloadFile(String keyName) {
        try {
            S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, keyName));
            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos;
        } catch (IOException ioe) {
            LOGGER.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException ase) {
            LOGGER.info("sCaught an AmazonServiceException from GET requests, rejected reasons:");
            LOGGER.info("Error Message:    " + ase.getMessage());
            LOGGER.info("HTTP Status Code: " + ase.getStatusCode());
            LOGGER.info("AWS Error Code:   " + ase.getErrorCode());
            LOGGER.info("Error Type:       " + ase.getErrorType());
            LOGGER.info("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            LOGGER.info("Caught an AmazonClientException: ");
            LOGGER.info("Error Message: " + ace.getMessage());
            throw ace;
        }

        return null;
    }
    
    public void uploadFile(String keyName, MultipartFile file) {

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3client.putObject(bucketName, keyName, file.getInputStream(), metadata);
        } catch (IOException ioe) {
            LOGGER.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException ase) {
            LOGGER.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            LOGGER.info("Error Message:    " + ase.getMessage());
            LOGGER.info("HTTP Status Code: " + ase.getStatusCode());
            LOGGER.info("AWS Error Code:   " + ase.getErrorCode());
            LOGGER.info("Error Type:       " + ase.getErrorType());
            LOGGER.info("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            LOGGER.info("Caught an AmazonClientException: ");
            LOGGER.info("Error Message: " + ace.getMessage());
            throw ace;
        }
    }

    public List<String> listFiles() {

        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName);
        //.withPrefix("test" + "/");

        List<String> keys = new ArrayList<>();

        ObjectListing objects = s3client.listObjects(listObjectsRequest);

        while (true) {
            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
            if (summaries.size() < 1) {
                break;
            }

            for (S3ObjectSummary item : summaries) {
                if (!item.getKey().endsWith("/"))
                    keys.add(item.getKey());
            }

            objects = s3client.listNextBatchOfObjects(objects);
        }

        return keys;
    }
    
    public void deleteFileFromS3Bucket(String keyName) {
        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (AmazonServiceException ex) {
            LOGGER.error("error [" + ex.getMessage() + "] occurred while removing [" + keyName + "] ");
        }


    }

    
    public void putObject(String keyName, ByteArrayInputStream bi, ObjectMetadata ob) throws IOException {
        try {
            s3client.putObject(bucketName, keyName, bi, ob);
        } catch (AmazonServiceException ase) {
            LOGGER.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            LOGGER.info("Error Message:    " + ase.getMessage());
            LOGGER.info("HTTP Status Code: " + ase.getStatusCode());
            LOGGER.info("AWS Error Code:   " + ase.getErrorCode());
            LOGGER.info("Error Type:       " + ase.getErrorType());
            LOGGER.info("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            LOGGER.info("Caught an AmazonClientException: ");
            LOGGER.info("Error Message: " + ace.getMessage());
            throw ace;
        }

    }
}

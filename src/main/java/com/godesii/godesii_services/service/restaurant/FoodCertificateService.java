//package com.godesii.godesii_services.service.restaurant;
//
//import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
//import com.godesii.godesii_services.exception.ResourceNotFoundException;
//import com.godesii.godesii_services.repository.restaurant.FoodCertificateRepo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class FoodCertificateService {
//
//    private final FoodCertificateRepo repo;
//
//    public FoodCertificateService(FoodCertificateRepo repo) {
//        this.repo = repo;
//    }
//
//    public FoodCertificate create(FoodCertificate cert) {
//        validateCertificate(cert);
//        FoodCertificate saved = repo.save(cert);
////        log.info("Created FoodCertificate: {}", saved);
//        return saved;
//    }
//
//    public List<FoodCertificate> getAll() {
//        List<FoodCertificate> certificates = repo.findAll();
////        log.info("Fetched {} FoodCertificates", certificates.size());
//        return certificates;
//    }
//
//    public FoodCertificate getById(Long id) {
//        return repo.findById(id)
//                .orElseThrow(() -> {
////                    log.warn("FoodCertificate with ID {} not found", id);
//                    return new ResourceNotFoundException("FoodCertificate not found with ID: " + id);
//                });
//    }
//
//    public FoodCertificate update(Long id, FoodCertificate cert) {
//        FoodCertificate existing = getById(id);
//
//        if (StringUtils.hasText(cert.getCertificateUrl())) {
//            existing.setCertificateUrl(cert.getCertificateUrl());
//        }
//        if (StringUtils.hasText(cert.getCertificateType())) {
//            existing.setCertificateType(cert.getCertificateType());
//        }
//
//        Optional.ofNullable(cert.getIssuedDate()).ifPresent(existing::setIssuedDate);
//        Optional.ofNullable(cert.getExpireDate()).ifPresent(existing::setExpireDate);
//
//        existing.setCertificateExpired(cert.isCertificateExpired());
//
//        FoodCertificate updated = repo.save(existing);
////        log.info("Updated FoodCertificate with ID {}: {}", id, updated);
//        return updated;
//    }
//
//    public void delete(Long id) {
//        FoodCertificate existing = getById(id);
//        repo.delete(existing);
////        log.info("Deleted FoodCertificate with ID {}", id);
//    }
//
//    private void validateCertificate(FoodCertificate cert) {
//        if (!StringUtils.hasText(cert.getCertificateUrl()) ||
//                !StringUtils.hasText(cert.getCertificateType()) ||
//                cert.getIssuedDate() == null ||
//                cert.getExpireDate() == null) {
//
////            log.error("Invalid FoodCertificate data: {}", cert);
//            throw new IllegalArgumentException("Missing required fields in FoodCertificate");
//        }
//    }
//}

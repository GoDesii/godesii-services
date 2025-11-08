package com.godesii.godesii_services.service;


import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
import com.godesii.godesii_services.repository.restaurant.FoodCertificateRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodCertificateService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FoodCertificateService.class);

    private final FoodCertificateRepo repo;

    public FoodCertificateService(FoodCertificateRepo repo) {
        this.repo = repo;
    }

    public FoodCertificate create(FoodCertificate cert) {
        return repo.save(cert);
    }

    public List<FoodCertificate> getAll() {
        return repo.findAll();
    }

    public FoodCertificate getById(Long id) {
        return repo.findById(id).orElseThrow(()-> new IllegalStateException("FoodCertificate not found"));
    }

    public FoodCertificate update(Long id, FoodCertificate cert) {
        FoodCertificate existing = getById(id);

        Optional.ofNullable(cert.getCertificateUrl()).ifPresent(existing::setCertificateUrl);
        Optional.ofNullable(cert.getCertificateType()).ifPresent(existing::setCertificateType);
        Optional.ofNullable(cert.getIssuedDate()).ifPresent(existing::setIssuedDate);
        Optional.ofNullable(cert.getExpireDate()).ifPresent(existing::setExpireDate);

        existing.setCertificateExpired(cert.isCertificateExpired());
        log.info("Updated FoodCertificate req {}",existing);
        return repo.save(existing);
    }


    public void delete(Long id) {
        repo.deleteById(id);
    }
}

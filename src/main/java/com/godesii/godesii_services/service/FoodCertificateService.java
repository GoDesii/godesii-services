package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
import com.godesii.godesii_services.repository.restaurant.FoodCertificateRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodCertificateService {

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
        return repo.findById(id).orElseThrow();
    }

    public FoodCertificate update(Long id, FoodCertificate cert) {
        FoodCertificate existing = getById(id);
        existing.setCertificateUrl(cert.getCertificateUrl());
        existing.setCertificateType(cert.getCertificateType());
        existing.setIssuedDate(cert.getIssuedDate());
        existing.setExpireDate(cert.getExpireDate());
        existing.setCertificateExpired(cert.isCertificateExpired());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

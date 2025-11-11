package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
import com.godesii.godesii_services.service.restaurant.FoodCertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(FoodCertificateController.ENDPOINT)
public class FoodCertificateController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/certificates";

    private final FoodCertificateService service;

    public FoodCertificateController(FoodCertificateService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FoodCertificate> create(@RequestBody FoodCertificate cert) {
        FoodCertificate created = service.create(cert);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FoodCertificate>> getAll() {
        List<FoodCertificate> certificates = service.getAll();
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodCertificate> getById(@PathVariable Long id) {
        FoodCertificate cert = service.getById(id);
        return cert != null ? ResponseEntity.ok(cert) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodCertificate> update(@PathVariable Long id, @RequestBody FoodCertificate cert) {
        FoodCertificate updated = service.update(id, cert);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

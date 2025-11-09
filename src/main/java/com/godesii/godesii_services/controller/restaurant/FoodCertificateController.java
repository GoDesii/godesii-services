package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
import com.godesii.godesii_services.service.FoodCertificateService;
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
    public FoodCertificate create(@RequestBody FoodCertificate cert) {
        return service.create(cert);
    }

    @GetMapping
    public List<FoodCertificate> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public FoodCertificate getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public FoodCertificate update(@PathVariable Long id, @RequestBody FoodCertificate cert) {
        return service.update(id, cert);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

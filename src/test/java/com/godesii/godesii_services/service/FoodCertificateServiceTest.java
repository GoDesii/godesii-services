package com.godesii.godesii_services.service;

import static org.junit.jupiter.api.Assertions.*;

import com.godesii.godesii_services.entity.restaurant.FoodCertificate;
import com.godesii.godesii_services.repository.restaurant.FoodCertificateRepo;
import com.godesii.godesii_services.service.restaurant.FoodCertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class FoodCertificateServiceTest {

    @Mock
    private FoodCertificateRepo repository;

    @InjectMocks
    private FoodCertificateService service;

    private FoodCertificate cert;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cert = new FoodCertificate();
        cert.setId(2L);
        cert.setCertificateUrl("http://certs.com/cert1.pdf");
        cert.setCertificateType("Health");
        cert.setIssuedDate(new Date());
        cert.setExpireDate(new Date());
        cert.setCertificateExpired(false);
    }

    @Test
    void testCreateCertificate() {
        when(repository.save(any(FoodCertificate.class))).thenReturn(cert);
        FoodCertificate saved = service.create(cert);
        assertEquals("Health", saved.getCertificateType());
    }

    @Test
    void testGetAllCertificates() {
        when(repository.findAll()).thenReturn(List.of(cert));
        List<FoodCertificate> list = service.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void testGetById() {
        when(repository.findById(2L)).thenReturn(Optional.of(cert));
        FoodCertificate found = service.getById(2L);
        assertEquals("Health", found.getCertificateType());
    }

    @Test
    void testUpdateCertificate() {
        when(repository.findById(2L)).thenReturn(Optional.of(cert));
        when(repository.save(any(FoodCertificate.class))).thenReturn(cert);

        FoodCertificate update = new FoodCertificate();
        update.setCertificateType("Updated Health");
        FoodCertificate updated = service.update(2L, update);

        assertEquals("Updated Health", updated.getCertificateType());
    }

//    @Test
//    void testDeleteCertificate() {
//        doNothing().when(repository).deleteById(2L);
//        service.delete(2L);
//        verify(repository, times(1)).deleteById(2L);
//    }
}

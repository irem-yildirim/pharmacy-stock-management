package com.pharmacy.service;

import com.pharmacy.dao.ExpiryDAO;
import com.pharmacy.entity.Expiry;
import com.pharmacy.entity.Drug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExpiryService unit testleri.
 * SKT sınıflandırma mantığı (OK / CRITICAL / EXPIRED) mock DAO ile doğrulanır.
 */
public class ExpiryServiceTest {

    private ExpiryService expiryService;
    private List<Expiry> expiryStore;

    @BeforeEach
    void setUp() {
        expiryStore = new ArrayList<>();

        ExpiryDAO mockExpiryDAO = new ExpiryDAO() {
            @Override
            public List<Expiry> findAll() {
                return new ArrayList<>(expiryStore);
            }
            @Override
            public void update(Expiry expiry) {
                // Mock: değişikliği local listede uygula
                for (int i = 0; i < expiryStore.size(); i++) {
                    if (expiryStore.get(i).getDrug().getBarcode().equals(expiry.getDrug().getBarcode())) {
                        expiryStore.set(i, expiry);
                        return;
                    }
                }
            }
        };

        expiryService = new ExpiryService(mockExpiryDAO);
    }

    // Yardımcı: Test için expiry kaydı üretir
    private Expiry makeExpiry(Long id, LocalDate date) {
        Drug dummy = new Drug();
        dummy.setBarcode("BC" + id);
        return new Expiry(dummy, date, 0, "OK");
    }

    // --- Test 1: 15 gün kalan → CRITICAL olmalı ---
    @Test
    void testRefreshExpiry_Within30Days_ShouldBeCritical() {
        Expiry exp = makeExpiry(1L, LocalDate.now().plusDays(15));
        exp.setId(1L);
        expiryStore.add(exp);

        expiryService.refreshExpiry();

        assertEquals("CRITICAL", expiryStore.get(0).getStatus(),
                "15 gün içinde dolacak ilaç CRITICAL statüsünde olmalı.");
    }

    // --- Test 2: Geçmiş tarih → EXPIRED olmalı ---
    @Test
    void testRefreshExpiry_PastDate_ShouldBeExpired() {
        Expiry exp = makeExpiry(2L, LocalDate.now().minusDays(1));
        exp.setId(2L);
        expiryStore.add(exp);

        expiryService.refreshExpiry();

        assertEquals("EXPIRED", expiryStore.get(0).getStatus(),
                "SKT geçmiş ilaç EXPIRED statüsünde olmalı.");
    }

    // --- Test 3: 200 gün kalan → OK olmalı ---
    @Test
    void testRefreshExpiry_FarFuture_ShouldBeOK() {
        Expiry exp = makeExpiry(3L, LocalDate.now().plusDays(200));
        exp.setId(3L);
        expiryStore.add(exp);

        expiryService.refreshExpiry();

        assertEquals("OK", expiryStore.get(0).getStatus(),
                "200 gün sonra dolacak ilaç OK statüsünde olmalı.");
    }

    // --- Test 4: Tam sınır: 30 gün → CRITICAL ---
    @Test
    void testRefreshExpiry_ExactlyThreshold_ShouldBeCritical() {
        Expiry exp = makeExpiry(4L, LocalDate.now().plusDays(30));
        exp.setId(4L);
        expiryStore.add(exp);

        expiryService.refreshExpiry();

        assertEquals("CRITICAL", expiryStore.get(0).getStatus(),
                "Tam 30 gün kalan ilaç eşik değerinde CRITICAL olmalı.");
    }

    // --- Test 5: getCriticalDrugs yalnızca CRITICAL olanları döndürmeli ---
    @Test
    void testGetCriticalDrugs_ShouldReturnOnlyCritical() {
        Expiry critical = makeExpiry(5L, LocalDate.now().plusDays(10));
        critical.setId(5L);
        critical.setStatus("CRITICAL");

        Expiry ok = makeExpiry(6L, LocalDate.now().plusDays(200));
        ok.setId(6L);
        ok.setStatus("OK");

        expiryStore.add(critical);
        expiryStore.add(ok);

        List<Expiry> result = expiryService.getCriticalDrugs();

        assertEquals(1, result.size(), "Yalnızca 1 CRITICAL kayıt dönmeli.");
        assertEquals("CRITICAL", result.get(0).getStatus());
    }
}

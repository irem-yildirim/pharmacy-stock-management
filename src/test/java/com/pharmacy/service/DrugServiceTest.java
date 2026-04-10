package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DrugService unit testleri.
 * Mock DrugDAO kullanılarak veritabanına bağlanmadan servis mantığı test edilir.
 */
public class DrugServiceTest {

    private DrugService drugService;
    private DrugDAO mockDrugDAO;

    // Test sırasında kaydedilen son ilaç referansını tutarız
    private Drug lastSavedDrug;
    private Drug lastUpdatedDrug;

    @BeforeEach
    void setUp() {
        lastSavedDrug = null;
        lastUpdatedDrug = null;

        mockDrugDAO = new DrugDAO() {
            @Override
            public void save(Drug drug) {
                lastSavedDrug = drug;
            }

            @Override
            public void update(Drug drug) {
                lastUpdatedDrug = drug;
            }

            @Override
            public Drug findById(String barcode) {
                // Simüle: barkod "EXISTING" ise kayıtlı bir ilaç döndür
                if ("EXISTING".equals(barcode)) {
                    Drug existing = new Drug();
                    existing.setBarcode("EXISTING");
                    existing.setName("Mevcut İlaç");
                    existing.setStockQuantity(20);
                    return existing;
                }
                return null;
            }

            @Override
            public List<Drug> findAll() {
                return Collections.emptyList();
            }
        };

        drugService = new DrugService(mockDrugDAO);
    }

    // --- Test 1: Yeni ilaç ekleme ---
    @Test
    void testAddDrug_ShouldNotThrowExceptions() {
        Drug drug = new Drug();
        drug.setBarcode("TEST12345");
        drug.setName("Aspirin Test");
        drug.setCostPrice(new BigDecimal("10.0"));
        drug.setSellingPrice(new BigDecimal("15.0"));
        drug.setStockQuantity(100);

        assertDoesNotThrow(() -> drugService.addDrug(drug),
                "Geçerli bir ilaç için exception fırlatılmamalı.");
    }

    // --- Test 2: Yeni barkod → save() çağrılmalı ---
    @Test
    void testAddDrug_NewBarcode_ShouldCallSave() {
        Drug drug = new Drug();
        drug.setBarcode("NEW_BARCODE_99");
        drug.setName("Yeni İlaç");
        drug.setCostPrice(new BigDecimal("5.00"));
        drug.setSellingPrice(new BigDecimal("12.00"));
        drug.setStockQuantity(50);

        drugService.addDrug(drug);

        assertNotNull(lastSavedDrug, "Yeni barkodlu ilaç için save() çağrılmış olmalı.");
        assertEquals("NEW_BARCODE_99", lastSavedDrug.getBarcode());
        assertNull(lastUpdatedDrug, "save() çağrıldığında update() çağrılmamalı.");
    }

    // --- Test 3: Mevcut barkod → update() çağrılmalı (duplicate önlemi) ---
    @Test
    void testAddDrug_ExistingBarcode_ShouldCallUpdate() {
        Drug drug = new Drug();
        drug.setBarcode("EXISTING"); // mockDAO bu barkod için mevcut ilaç döndürür
        drug.setName("Güncellenen İlaç");
        drug.setCostPrice(new BigDecimal("8.00"));
        drug.setSellingPrice(new BigDecimal("20.00"));
        drug.setStockQuantity(30);

        drugService.addDrug(drug);

        assertNotNull(lastUpdatedDrug, "Mevcut barkod için update() çağrılmış olmalı.");
        assertNull(lastSavedDrug, "update() çağrıldığında save() çağrılmamalı.");
    }

    // --- Test 4: getAllDrugs boş liste döndürmeli ---
    @Test
    void testGetAllDrugs_ShouldReturnList() {
        List<Drug> results = drugService.getAllDrugs();

        assertNotNull(results, "İlaç listesi asla null olmamalı.");
        assertTrue(results.isEmpty(), "Mock DAO boş liste döndürür.");
    }

    // --- Test 5: findByBarcode null döndürmeli (bilinmeyen barkod) ---
    @Test
    void testFindByBarcode_UnknownBarcode_ShouldReturnNull() {
        Drug result = drugService.findByBarcode("NONEXISTENT");
        assertNull(result, "Bilinmeyen barkod için null dönmeli.");
    }

    // --- Test 6: findByBarcode mevcut ilaç döndürmeli ---
    @Test
    void testFindByBarcode_KnownBarcode_ShouldReturnDrug() {
        Drug result = drugService.findByBarcode("EXISTING");
        assertNotNull(result, "Mevcut barkod için Drug nesnesi dönmeli.");
        assertEquals("EXISTING", result.getBarcode());
    }
}

package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.PurchaseDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PurchaseService unit testleri.
 * Mock DAO'lar ile veritabanına bağlanmadan satın alma iş mantığı doğrulanır.
 */
public class PurchaseServiceTest {

    private PurchaseService purchaseService;

    // Mock takip değişkenleri
    private Drug storedDrug;
    private List<Purchase> savedPurchases;

    @BeforeEach
    void setUp() {
        savedPurchases = new ArrayList<>();

        // Başlangıç: stok = 30
        storedDrug = new Drug();
        storedDrug.setBarcode("DRUG002");
        storedDrug.setName("Satın Alma Test İlacı");
        storedDrug.setCostPrice(new BigDecimal("20.00"));
        storedDrug.setSellingPrice(new BigDecimal("45.00"));
        storedDrug.setStockQuantity(30);

        DrugDAO mockDrugDAO = new DrugDAO() {
            @Override
            public Drug findById(String barcode) {
                return "DRUG002".equals(barcode) ? storedDrug : null;
            }
            @Override
            public void update(Drug drug) {
                storedDrug.setStockQuantity(drug.getStockQuantity());
            }
            @Override public List<Drug> findAll() { return Collections.emptyList(); }
        };

        PurchaseDAO mockPurchaseDAO = new PurchaseDAO() {
            @Override
            public void save(Purchase purchase) {
                savedPurchases.add(purchase);
            }
            @Override
            public List<Purchase> findAll() {
                return new ArrayList<>(savedPurchases);
            }
        };

        purchaseService = new PurchaseService(mockPurchaseDAO, mockDrugDAO);
    }

    // --- Test 1: Satın alma sonrasında stok artmalı ---
    @Test
    void testAddPurchase_ShouldIncrementStock() {
        int oncekiStok = storedDrug.getStockQuantity(); // 30
        int alinanMiktar = 20;

        purchaseService.addPurchase("DRUG002", alinanMiktar);

        assertEquals(oncekiStok + alinanMiktar, storedDrug.getStockQuantity(),
                "Satın alma sonrası stok 30 + 20 = 50 olmalı.");
    }

    // --- Test 2: Satın alma kaydı oluşturulmalı ---
    @Test
    void testAddPurchase_ShouldCreatePurchaseRecord() {
        Purchase result = purchaseService.addPurchase("DRUG002", 15);

        assertNotNull(result, "Satın alma nesnesi null olmamalı.");
        assertEquals(15, result.getQuantityAdded(), "Satın alma miktarı 15 olmalı.");
        assertNotNull(result.getPurchaseDate(), "Satın alma tarihi dolu olmalı.");
    }

    // --- Test 3: Satın alma DAO'ya kaydedilmeli ---
    @Test
    void testAddPurchase_ShouldPersistToPurchaseDAO() {
        purchaseService.addPurchase("DRUG002", 10);

        assertEquals(1, savedPurchases.size(), "1 satın alma kaydı oluşmuş olmalı.");
        assertEquals(10, savedPurchases.get(0).getQuantityAdded());
    }

    // --- Test 4: Var olmayan ilaç için exception fırlatılmalı ---
    @Test
    void testAddPurchase_UnknownDrug_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.addPurchase("NONEXISTENT", 5),
                "Bilinmeyen barkod için IllegalArgumentException fırlatılmalı.");
    }

    // --- Test 5: calculateTotalPurchases doğru toplam döndürmeli ---
    @Test
    void testCalculateTotalPurchases_ShouldReturnCorrectTotal() {
        // 10 adet x 20 TL maliyet = 200 TL
        purchaseService.addPurchase("DRUG002", 10);

        BigDecimal total = purchaseService.calculateTotalPurchases();
        assertEquals(new BigDecimal("200.00"), total,
                "10 adet x 20 TL maliyet = 200 TL toplam satın alma bekleniyor.");
    }
}

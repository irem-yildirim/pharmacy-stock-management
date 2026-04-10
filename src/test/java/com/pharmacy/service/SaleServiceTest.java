package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.SaleItemDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SaleService unit testleri.
 * Mock DAO'lar ile veritabanına bağlanmadan satış iş mantığı doğrulanır.
 */
public class SaleServiceTest {

    private SaleService saleService;

    // Mock DAO'ların takip değişkenleri
    private Drug storedDrug;         // drugDAO.update() ile kaydedilen ilaç
    private Sale savedSale;          // saleDAO.save() ile kaydedilen satış
    private List<SaleItem> savedItems; // saleItemDAO.save() ile kaydedilen kalemler

    @BeforeEach
    void setUp() {
        savedItems = new ArrayList<>();

        // Test için başlangıç durumundaki ilaç: stok = 50
        storedDrug = new Drug();
        storedDrug.setBarcode("DRUG001");
        storedDrug.setName("Test İlaç");
        storedDrug.setCostPrice(new BigDecimal("10.00"));
        storedDrug.setSellingPrice(new BigDecimal("25.00"));
        storedDrug.setStockQuantity(50);

        DrugDAO mockDrugDAO = new DrugDAO() {
            @Override
            public Drug findById(String barcode) {
                return "DRUG001".equals(barcode) ? storedDrug : null;
            }
            @Override
            public void update(Drug drug) {
                // Stok değişikliğini simüle et
                storedDrug.setStockQuantity(drug.getStockQuantity());
            }
            @Override public List<Drug> findAll() { return Collections.emptyList(); }
        };

        SaleDAO mockSaleDAO = new SaleDAO() {
            @Override
            public void save(Sale sale) {
                sale.setId(1001L); // Simüle otomatik ID
                savedSale = sale;
            }
            @Override public List<Sale> findAll() {
                if (savedSale != null) {
                    List<Sale> list = new ArrayList<>();
                    list.add(savedSale);
                    return list;
                }
                return Collections.emptyList();
            }
        };

        SaleItemDAO mockSaleItemDAO = new SaleItemDAO() {
            @Override
            public void save(SaleItem item) {
                savedItems.add(item);
            }
            @Override public List<SaleItem> findAll() { return Collections.emptyList(); }
        };

        saleService = new SaleService(mockSaleDAO, mockSaleItemDAO, mockDrugDAO);
    }

    // --- Test 1: Satış sonrasında stok düşmeli ---
    @Test
    void testCreateSale_ShouldDeductStock() {
        SaleItem item = new SaleItem();
        item.setDrug(storedDrug);
        item.setQuantity(5);
        item.setUnitPrice(new BigDecimal("25.00"));

        List<SaleItem> items = new ArrayList<>();
        items.add(item);

        saleService.createSale(items);

        assertEquals(45, storedDrug.getStockQuantity(),
                "5 adet satıştan sonra stok 50'den 45'e düşmeli.");
    }

    // --- Test 2: Satış kaydı oluşturulmalı ---
    @Test
    void testCreateSale_ShouldCreateSaleRecord() {
        SaleItem item = new SaleItem();
        item.setDrug(storedDrug);
        item.setQuantity(3);
        item.setUnitPrice(new BigDecimal("25.00"));

        List<SaleItem> items = new ArrayList<>();
        items.add(item);

        Sale result = saleService.createSale(items);

        assertNotNull(result, "Satış nesnesi null olmamalı.");
        assertNotNull(result.getTotalAmount(), "Satış toplam tutarı hesaplanmış olmalı.");
        assertEquals(new BigDecimal("75.00"), result.getTotalAmount(),
                "3 adet x 25 TL = 75 TL toplam bekleniyor.");
    }

    // --- Test 3: SaleItem kayıt altına alınmalı ---
    @Test
    void testCreateSale_ShouldSaveSaleItems() {
        SaleItem item = new SaleItem();
        item.setDrug(storedDrug);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("25.00"));

        List<SaleItem> items = new ArrayList<>();
        items.add(item);

        saleService.createSale(items);

        assertEquals(1, savedItems.size(), "1 satış kalemi kaydedilmiş olmalı.");
        assertEquals(2, savedItems.get(0).getQuantity());
    }

    // --- Test 4: Var olmayan ilaç satışı exception fırlatmalı ---
    @Test
    void testCreateSale_UnknownDrug_ShouldThrowException() {
        Drug unknownDrug = new Drug();
        unknownDrug.setBarcode("NONEXISTENT");

        SaleItem item = new SaleItem();
        item.setDrug(unknownDrug);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("10.00"));

        List<SaleItem> items = new ArrayList<>();
        items.add(item);

        assertThrows(IllegalArgumentException.class,
                () -> saleService.createSale(items),
                "Bilinmeyen ilaç barkodu için exception fırlatılmalı.");
    }

    // --- Test 5: Bugünkü satış toplamı doğru hesaplanmalı ---
    @Test
    void testCalculateTodaySales_ShouldReturnCorrectTotal() {
        SaleItem item = new SaleItem();
        item.setDrug(storedDrug);
        item.setQuantity(4);
        item.setUnitPrice(new BigDecimal("25.00"));

        List<SaleItem> items = new ArrayList<>();
        items.add(item);

        saleService.createSale(items); // Bugünün satışı: 4 x 25 = 100 TL

        BigDecimal todayTotal = saleService.calculateTodaySales();
        assertEquals(new BigDecimal("100.00"), todayTotal,
                "Bugünün satış toplamı 100.00 TL olmalı.");
    }
}

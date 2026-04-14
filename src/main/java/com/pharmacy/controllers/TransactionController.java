package com.pharmacy.controllers;

import com.pharmacy.entity.*;
import com.pharmacy.service.SaleService;
import com.pharmacy.service.PurchaseService;
import com.pharmacy.service.DrugService;

import java.util.ArrayList;
import java.util.List;

/**
 * Alış (Purchase) ve Satış (Sell) işlemlerini koordine eder.
 * UI'dan gelen "sat" veya "al" komutunu alıp servis katmanına iletir.
 */
public class TransactionController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    // İlaç bilgisine doğrudan erişmek için drugService de burada tutuyoruz
    private final DrugService drugService; 

    public TransactionController(SaleService saleService, PurchaseService purchaseService, DrugService drugService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.drugService = drugService;
    }

    // Eczacı barkod okutup satış yaptığında bu metot devreye giriyor
    public boolean sellDrug(String barcode, int quantity) {
        try {
            // Önce ilacı veritabanından çekiyoruz, yoksa veya stok yetersizse direkt false dönüyoruz
            Drug d = drugService.findByBarcode(barcode);
            if (d == null || d.getStockQuantity() < quantity) return false;

            // Satış kalemi oluşturuyoruz — sepete atmak gibi düşün
            SaleItem item = new SaleItem();
            item.setDrug(d);
            item.setQuantity(quantity);
            item.setUnitPrice(d.getSellingPrice());

            // Sepeti listeye koyup satışı tamamlatıyoruz
            List<SaleItem> items = new ArrayList<>();
            items.add(item);
            saleService.createSale(items);
            return true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            // İş kuralı hataları (yetersiz stok vb.) View'a iletiliyor, orada popup çıkar
            throw e;
        } catch (Exception e) {
            // Beklenmedik hata olursa log'a yazılır ve false dönülür
            e.printStackTrace();
            return false;
        }
    }

    // Eczane toptancıdan ilaç aldığında bu metot çalışıyor — stok artışı burada başlar
    public boolean purchaseDrug(String barcode, int quantity) {
        try {
            purchaseService.addPurchase(barcode, quantity);
            return true;
        } catch (IllegalArgumentException e) {
            // Geçersiz barkod veya negatif miktar gelirse hata UI'ya fırlatılıyor
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

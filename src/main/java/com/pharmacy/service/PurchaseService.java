package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.PurchaseDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Purchase;

import java.util.List;

/**
 * Eczanenin toptancıdan ilaç satın alma (Purchase) işlemini yöneten servis.
 * Alınan ilaç stoğa eklenir ve kayıt veritabanına işlenir.
 */
public class PurchaseService {

    private final PurchaseDAO purchaseDAO;
    // Stok güncellemesi yapabilmek için DrugDAO da burada lazım
    private final DrugDAO drugDAO;

    public PurchaseService(PurchaseDAO purchaseDAO, DrugDAO drugDAO) {
        this.purchaseDAO = purchaseDAO;
        this.drugDAO = drugDAO;
    }

    // Yeni bir ilaç alımı kaydeder ve stok miktarını günceller
    public Purchase addPurchase(String barcode, int quantity) {
        // Defansif Kontrol: Negatif veya sıfır alım miktarı engellenir — sıfır kutu alamazsın!
        if (quantity <= 0) {
            throw new IllegalArgumentException("Purchase quantity must be positive. Entered: " + quantity);
        }

        // İlaç veritabanında var mı diye kontrol ediyoruz
        Drug drug = drugDAO.findById(barcode);
        if (drug == null) {
            throw new IllegalArgumentException("Drug not found: " + barcode);
        }

        // Alım kaydını oluşturuyoruz
        Purchase purchase = new Purchase();
        purchase.setDrug(drug);
        purchase.setQuantityAdded(quantity);
        purchase.setPurchaseDate(java.time.LocalDate.now()); // Alım tarihi bugün

        // Stoğa eklenen kutu sayısını eski stokun üstüne ekliyoruz
        drug.setStockQuantity(drug.getStockQuantity() + quantity);
        drugDAO.update(drug);

        // Hem alım kaydını hem güncellenen stoku veritabanına yazıyoruz
        purchaseDAO.save(purchase);
        return purchase;
    }

    // Tüm geçmiş alımları getiriyoruz — Finans raporu için gerekiyor
    public List<Purchase> getAllPurchases() {
        return purchaseDAO.findAll();
    }

    // Tüm alımların toplam maliyetini hesaplıyoruz (miktar x alış fiyatı)
    public java.math.BigDecimal calculateTotalPurchases() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        List<Purchase> purchases = getAllPurchases();
        for (Purchase p : purchases) {
            if (p.getDrug() != null && p.getDrug().getBarcode() != null) {
                // Her alım kaydı için ilacın alış fiyatını güncel halinden çekiyoruz
                Drug drug = drugDAO.findById(p.getDrug().getBarcode());
                if (drug != null && drug.getCostPrice() != null) {
                    // Satır maliyeti = alış fiyatı x alınan miktar
                    java.math.BigDecimal rowCost = drug.getCostPrice().multiply(java.math.BigDecimal.valueOf(p.getQuantityAdded()));
                    total = total.add(rowCost);
                }
            }
        }
        return total;
    }
}

package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.PurchaseDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Purchase;

import java.util.List;

public class PurchaseService {

    private final PurchaseDAO purchaseDAO;
    private final DrugDAO drugDAO;

    public PurchaseService(PurchaseDAO purchaseDAO, DrugDAO drugDAO) {
        this.purchaseDAO = purchaseDAO;
        this.drugDAO = drugDAO;
    }

    public Purchase addPurchase(String barcode, int quantity) {
        // Defansif Kontrol: Negatif veya sıfır alım miktarı engellenir
        if (quantity <= 0) {
            throw new IllegalArgumentException("Purchase quantity must be positive. Entered: " + quantity);
        }

        Drug drug = drugDAO.findById(barcode);
        if (drug == null) {
            throw new IllegalArgumentException("Drug not found: " + barcode);
        }

        Purchase purchase = new Purchase();
        purchase.setDrug(drug);
        purchase.setQuantityAdded(quantity);
        purchase.setPurchaseDate(java.time.LocalDate.now());

        drug.setStockQuantity(drug.getStockQuantity() + quantity);
        drugDAO.update(drug);

        purchaseDAO.save(purchase);
        return purchase;
    }

    public List<Purchase> getAllPurchases() {
        return purchaseDAO.findAll();
    }



    public java.math.BigDecimal calculateTotalPurchases() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        List<Purchase> purchases = getAllPurchases();
        for (Purchase p : purchases) {
            if (p.getDrug() != null && p.getDrug().getBarcode() != null) {
                Drug drug = drugDAO.findById(p.getDrug().getBarcode());
                if (drug != null && drug.getCostPrice() != null) {
                    java.math.BigDecimal rowCost = drug.getCostPrice().multiply(java.math.BigDecimal.valueOf(p.getQuantityAdded()));
                    total = total.add(rowCost);
                }
            }
        }
        return total;
    }
}

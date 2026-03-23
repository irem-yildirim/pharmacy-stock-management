package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.PurchaseDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Purchase;
import com.pharmacy.pattern.AppLogger;
import com.pharmacy.pattern.TransactionFactory;

import java.util.List;

public class PurchaseService {

    private final PurchaseDAO purchaseDAO;
    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public PurchaseService(PurchaseDAO purchaseDAO, DrugDAO drugDAO) {
        this.purchaseDAO = purchaseDAO;
        this.drugDAO = drugDAO;
    }

    public Purchase addPurchase(String barcode, int quantity) {
        logger.log("Recording purchase: barcode=" + barcode + ", qty=" + quantity);

        Drug drug = drugDAO.findById(barcode);
        if (drug == null) {
            throw new IllegalArgumentException("Drug not found: " + barcode);
        }

        Purchase purchase = TransactionFactory.createPurchase(drug, quantity);

        drug.setStockQuantity(drug.getStockQuantity() + quantity);
        drugDAO.update(drug);

        purchaseDAO.save(purchase);
        logger.log("Purchase recorded. New stock for " + drug.getName() + ": " + drug.getStockQuantity());
        return purchase;
    }

    public List<Purchase> getAllPurchases() {
        logger.log("Fetching all purchases");
        return purchaseDAO.findAll();
    }

    public List<Purchase> getPurchasesByDrug(String barcode) {
        logger.log("Fetching purchases for drug: " + barcode);
        List<Purchase> all = purchaseDAO.findAll();
        all.removeIf(p -> p.getDrug() == null || !barcode.equals(p.getDrug().getBarcode()));
        return all;
    }
}

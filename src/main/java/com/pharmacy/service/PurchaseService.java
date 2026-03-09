package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.PurchaseDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Purchase;
import com.pharmacy.pattern.AppLogger;
import com.pharmacy.pattern.TransactionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Purchase (stock restock) operations.
 * Uses {@link TransactionFactory} to create Purchase objects (Factory Pattern).
 */
@Service
public class PurchaseService {

    private final PurchaseDAO purchaseDAO;
    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public PurchaseService(PurchaseDAO purchaseDAO, DrugDAO drugDAO) {
        this.purchaseDAO = purchaseDAO;
        this.drugDAO = drugDAO;
    }

    /**
     * Records a drug purchase and updates the drug's stock quantity.
     *
     * @param barcode  the drug's barcode
     * @param quantity number of units purchased
     * @return the saved Purchase entity
     * @throws IllegalArgumentException if drug not found
     */
    @Transactional
    public Purchase addPurchase(String barcode, int quantity) {
        logger.log("Recording purchase: barcode=" + barcode + ", qty=" + quantity);

        Drug drug = drugDAO.findById(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Drug not found: " + barcode));

        // Use TransactionFactory — Factory Pattern
        Purchase purchase = TransactionFactory.createPurchase(drug, quantity);

        // Update stock
        drug.setStockQuantity(drug.getStockQuantity() + quantity);
        drugDAO.save(drug);

        Purchase saved = purchaseDAO.save(purchase);
        logger.log("Purchase recorded. New stock for " + drug.getName() + ": " + drug.getStockQuantity());
        return saved;
    }

    public List<Purchase> getAllPurchases() {
        logger.log("Fetching all purchases");
        return purchaseDAO.findAll();
    }

    public List<Purchase> getPurchasesByDrug(String barcode) {
        logger.log("Fetching purchases for drug: " + barcode);
        return purchaseDAO.findByDrug_Barcode(barcode);
    }
}

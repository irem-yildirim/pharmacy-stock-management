package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.ExpiryDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Expiry;
import com.pharmacy.pattern.AppLogger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service class for Expiry tracking.
 * Computes days remaining until expiration and classifies drugs by status.
 */
@Service
public class ExpiryService {

    /** Threshold (days) below which a drug is classified as "CRITICAL". */
    private static final int CRITICAL_THRESHOLD_DAYS = 30;

    private final ExpiryDAO expiryDAO;
    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public ExpiryService(ExpiryDAO expiryDAO, DrugDAO drugDAO) {
        this.expiryDAO = expiryDAO;
        this.drugDAO = drugDAO;
    }

    /**
     * Recalculates daysRemaining and status for all drugs in inventory.
     * Status logic:
     * - daysRemaining <= 0 → "EXPIRED"
     * - daysRemaining <= 30 → "CRITICAL"
     * - otherwise → "OK"
     */
    @Transactional
    public void refreshExpiry() {
        logger.log("Refreshing expiry records for all drugs...");
        List<Drug> drugs = drugDAO.findAll();

        for (Drug drug : drugs) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), drug.getExpirationDate());

            String status;
            if (daysRemaining <= 0) {
                status = "EXPIRED";
            } else if (daysRemaining <= CRITICAL_THRESHOLD_DAYS) {
                status = "CRITICAL";
            } else {
                status = "OK";
            }

            Expiry expiry = expiryDAO.findByDrug_Barcode(drug.getBarcode())
                    .orElse(new Expiry());

            expiry.setDrug(drug);
            expiry.setDaysRemaining(daysRemaining);
            expiry.setStatus(status);
            expiryDAO.save(expiry);
        }

        logger.log("Expiry refresh complete. " + drugs.size() + " drug(s) processed.");
    }

    public List<Expiry> getExpiredDrugs() {
        logger.log("Fetching EXPIRED drugs");
        return expiryDAO.findByStatus("EXPIRED");
    }

    public List<Expiry> getCriticalDrugs() {
        logger.log("Fetching CRITICAL drugs");
        return expiryDAO.findByStatus("CRITICAL");
    }

    public List<Expiry> getAllExpiry() {
        logger.log("Fetching all expiry records");
        return expiryDAO.findAll();
    }
}

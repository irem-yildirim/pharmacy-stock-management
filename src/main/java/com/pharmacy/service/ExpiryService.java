package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.ExpiryDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Expiry;
import com.pharmacy.pattern.AppLogger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExpiryService {

    private static final int CRITICAL_THRESHOLD_DAYS = 30;

    private final ExpiryDAO expiryDAO;
    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public ExpiryService(ExpiryDAO expiryDAO, DrugDAO drugDAO) {
        this.expiryDAO = expiryDAO;
        this.drugDAO = drugDAO;
    }

    public void refreshExpiry() {
        logger.log("Refreshing expiry records for all drugs...");
        List<Drug> drugs = drugDAO.findAll();

        for (Drug drug : drugs) {
            if (drug.getExpirationDate() == null) continue;
            
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), drug.getExpirationDate());

            String status;
            if (daysRemaining <= 0) {
                status = "EXPIRED";
            } else if (daysRemaining <= CRITICAL_THRESHOLD_DAYS) {
                status = "CRITICAL";
            } else {
                status = "OK";
            }

            Expiry expiry = expiryDAO.findByDrugBarcode(drug.getBarcode());
            if (expiry == null) {
                expiry = new Expiry();
                expiry.setDrug(drug);
                expiry.setDaysRemaining(daysRemaining);
                expiry.setStatus(status);
                expiryDAO.save(expiry);
            } else {
                expiry.setDaysRemaining(daysRemaining);
                expiry.setStatus(status);
                expiryDAO.update(expiry);
            }
        }

        logger.log("Expiry refresh complete. " + drugs.size() + " drug(s) processed.");
    }

    public List<Expiry> getExpiredDrugs() {
        logger.log("Fetching EXPIRED drugs");
        List<Expiry> all = expiryDAO.findAll();
        all.removeIf(e -> !"EXPIRED".equals(e.getStatus()));
        return all;
    }

    public List<Expiry> getCriticalDrugs() {
        logger.log("Fetching CRITICAL drugs");
        List<Expiry> all = expiryDAO.findAll();
        all.removeIf(e -> !"CRITICAL".equals(e.getStatus()));
        return all;
    }

    public List<Expiry> getAllExpiry() {
        logger.log("Fetching all expiry records");
        return expiryDAO.findAll();
    }
}

package com.pharmacy.service;

import com.pharmacy.dao.ExpiryDAO;
import com.pharmacy.entity.Expiry;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExpiryService {

    private static final int CRITICAL_THRESHOLD_DAYS = 30;

    private final ExpiryDAO expiryDAO;

    public ExpiryService(ExpiryDAO expiryDAO) {
        this.expiryDAO = expiryDAO;
    }

    /**
     * Refreshes the daysRemaining and status for all Expiry records.
     * It relies entirely on the Expiry table, completely decoupled from Drug tracking.
     */
    public void refreshExpiry() {

        List<Expiry> expiries = expiryDAO.findAll();

        for (Expiry expiry : expiries) {
            if (expiry.getExpirationDate() == null) continue;
            
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expiry.getExpirationDate());

            String status;
            if (daysRemaining <= 0) {
                status = "EXPIRED";
            } else if (daysRemaining <= CRITICAL_THRESHOLD_DAYS) {
                status = "CRITICAL";
            } else {
                status = "OK";
            }

            expiry.setDaysRemaining(daysRemaining);
            expiry.setStatus(status);
            expiryDAO.update(expiry);
        }

    }

    public List<Expiry> getExpiredDrugs() {
        List<Expiry> all = expiryDAO.findAll();
        all.removeIf(e -> !"EXPIRED".equals(e.getStatus()));
        return all;
    }

    public List<Expiry> getCriticalDrugs() {
        List<Expiry> all = expiryDAO.findAll();
        all.removeIf(e -> !"CRITICAL".equals(e.getStatus()));
        return all;
    }

    public List<Expiry> getAllExpiry() {
        return expiryDAO.findAll();
    }
}

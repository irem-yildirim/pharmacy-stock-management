package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.pattern.AppLogger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Drug business operations.
 * All business logic for drug management lives here — controllers are kept
 * thin.
 */
@Service
public class DrugService {

    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    /** Constructor injection — preferred over field injection for testability. */
    public DrugService(DrugDAO drugDAO) {
        this.drugDAO = drugDAO;
    }

    public Drug addDrug(Drug drug) {
        logger.log("Adding drug: " + drug.getName() + " [" + drug.getBarcode() + "]");
        return drugDAO.save(drug);
    }

    public Drug updateDrug(Drug drug) {
        logger.log("Updating drug: " + drug.getBarcode());
        return drugDAO.save(drug);
    }

    public void deleteDrug(String barcode) {
        logger.log("Deleting drug: " + barcode);
        drugDAO.deleteById(barcode);
    }

    public List<Drug> getAllDrugs() {
        logger.log("Fetching all drugs");
        return drugDAO.findAll();
    }

    public Optional<Drug> findByBarcode(String barcode) {
        logger.log("Looking up drug by barcode: " + barcode);
        return drugDAO.findById(barcode);
    }

    public List<Drug> findExpiringSoon(int daysThreshold) {
        logger.log("Fetching drugs expiring within " + daysThreshold + " days");
        LocalDate threshold = LocalDate.now().plusDays(daysThreshold);
        return drugDAO.findByExpirationDateBefore(threshold);
    }

    public List<Drug> searchByName(String name) {
        logger.log("Searching drugs by name: " + name);
        return drugDAO.findByNameContainingIgnoreCase(name);
    }

    public List<Drug> findByCategory(Long categoryId) {
        logger.log("Fetching drugs for category ID: " + categoryId);
        return drugDAO.findByCategory_Id(categoryId);
    }

    public List<Drug> findByPrescriptionType(String prescriptionType) {
        logger.log("Fetching drugs with prescription type: " + prescriptionType);
        return drugDAO.findByPrescriptionType(prescriptionType);
    }
}

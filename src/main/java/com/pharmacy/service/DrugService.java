package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.pattern.AppLogger;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DrugService {

    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public DrugService(DrugDAO drugDAO) {
        this.drugDAO = drugDAO;
    }

    public Drug addDrug(Drug drug) {
        logger.log("Adding drug: " + drug.getName() + " [" + drug.getBarcode() + "]");
        if (drugDAO.findById(drug.getBarcode()) != null) {
            drugDAO.update(drug);
        } else {
            drugDAO.save(drug);
        }
        return drug;
    }

    public Drug updateDrug(Drug drug) {
        logger.log("Updating drug: " + drug.getBarcode());
        drugDAO.update(drug);
        return drug;
    }

    public void deleteDrug(String barcode) {
        logger.log("Deleting drug: " + barcode);
        drugDAO.delete(barcode);
    }

    public List<Drug> getAllDrugs() {
        logger.log("Fetching all drugs");
        return drugDAO.findAll();
    }

    public Drug findByBarcode(String barcode) {
        logger.log("Looking up drug by barcode: " + barcode);
        return drugDAO.findById(barcode);
    }

    public List<Drug> findExpiringSoon(int daysThreshold) {
        logger.log("Fetching drugs expiring within " + daysThreshold + " days");
        LocalDate threshold = LocalDate.now().plusDays(daysThreshold);
        return drugDAO.findAll().stream()
                .filter(d -> d.getExpirationDate() != null && d.getExpirationDate().isBefore(threshold))
                .collect(Collectors.toList());
    }

    public List<Drug> searchByName(String name) {
        logger.log("Searching drugs by name: " + name);
        String lowerName = name.toLowerCase();
        return drugDAO.findAll().stream()
                .filter(d -> d.getName() != null && d.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    public List<Drug> findByCategory(Long categoryId) {
        logger.log("Fetching drugs for category ID: " + categoryId);
        return java.util.Collections.emptyList();
    }

    public List<Drug> findByPrescriptionType(String prescriptionType) {
        logger.log("Fetching drugs with prescription type: " + prescriptionType);
        return drugDAO.findAll().stream()
                .filter(d -> prescriptionType.equals(d.getPrescriptionType()))
                .collect(Collectors.toList());
    }
}

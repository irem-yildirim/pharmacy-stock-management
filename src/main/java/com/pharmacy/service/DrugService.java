package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;

import java.util.List;
import java.util.stream.Collectors;

public class DrugService {

    private final DrugDAO drugDAO;

    public DrugService(DrugDAO drugDAO) {
        this.drugDAO = drugDAO;
    }

    public Drug addDrug(Drug drug) {
        System.out.println("[DrugService] Adding drug: " + drug.getName() + " [" + drug.getBarcode() + "]");
        if (drugDAO.findById(drug.getBarcode()) != null) {
            drugDAO.update(drug);
        } else {
            drugDAO.save(drug);
        }
        return drug;
    }

    public Drug updateDrug(Drug drug) {
        System.out.println("[DrugService] Updating drug: " + drug.getBarcode());
        drugDAO.update(drug);
        return drug;
    }

    public void deleteDrug(String barcode) {
        System.out.println("[DrugService] Deleting drug: " + barcode);
        drugDAO.delete(barcode);
    }

    public List<Drug> getAllDrugs() {
        System.out.println("[DrugService] Fetching all drugs");
        return drugDAO.findAll();
    }

    public Drug findByBarcode(String barcode) {
        System.out.println("[DrugService] Looking up drug by barcode: " + barcode);
        return drugDAO.findById(barcode);
    }

    public List<Drug> searchByName(String name) {
        System.out.println("[DrugService] Searching drugs by name: " + name);
        String lowerName = name.toLowerCase();
        return drugDAO.findAll().stream()
                .filter(d -> d.getName() != null && d.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    public List<Drug> findByCategory(Long categoryId) {
        System.out.println("[DrugService] Fetching drugs for category ID: " + categoryId);
        return drugDAO.findAll().stream()
                .filter(d -> d.getCategory() != null && categoryId.equals(d.getCategory().getId()))
                .collect(Collectors.toList());
    }
}

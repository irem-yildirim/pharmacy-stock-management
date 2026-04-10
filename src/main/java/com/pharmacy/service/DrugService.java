package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;

import java.util.List;


public class DrugService {

    private final DrugDAO drugDAO;

    public DrugService(DrugDAO drugDAO) {
        this.drugDAO = drugDAO;
    }

    public Drug addDrug(Drug drug) {
        if (drugDAO.findById(drug.getBarcode()) != null) {
            drugDAO.update(drug);
        } else {
            drugDAO.save(drug);
        }
        return drug;
    }

    public Drug updateDrug(Drug drug) {
        drugDAO.update(drug);
        return drug;
    }

    public void deleteDrug(String barcode) {
        drugDAO.delete(barcode);
    }

    public List<Drug> getAllDrugs() {
        return drugDAO.findAll();
    }

    public Drug findByBarcode(String barcode) {
        return drugDAO.findById(barcode);
    }

}

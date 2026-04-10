package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.SaleItemDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;
import java.math.BigDecimal;
import java.util.List;

public class SaleService {

    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;
    private final DrugDAO drugDAO;

    public SaleService(SaleDAO saleDAO, SaleItemDAO saleItemDAO, DrugDAO drugDAO) {
        this.saleDAO = saleDAO;
        this.saleItemDAO = saleItemDAO;
        this.drugDAO = drugDAO;
    }

    public Sale createSale(List<SaleItem> items) {


        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : items) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);

            Drug drug = item.getDrug();
            if (drug != null && drug.getBarcode() != null) {
                Drug persistedDrug = drugDAO.findById(drug.getBarcode());
                if (persistedDrug == null) {
                    throw new IllegalArgumentException("Drug not found: " + drug.getBarcode());
                }
                persistedDrug.setStockQuantity(persistedDrug.getStockQuantity() - item.getQuantity());
                drugDAO.update(persistedDrug);
                item.setDrug(persistedDrug);
            }
        }

        Sale sale = new Sale();
        sale.setTotalAmount(total);
        sale.setSaleDate(java.time.LocalDate.now());
        saleDAO.save(sale);

        for (SaleItem item : items) {
            item.setSale(sale);
            saleItemDAO.save(item);
        }

        return sale;
    }

    public List<Sale> getAllSales() {
        return saleDAO.findAll();
    }

    public BigDecimal calculateTotalSales() {
        BigDecimal total = BigDecimal.ZERO;
        for (Sale s : getAllSales()) {
            if (s.getTotalAmount() != null) {
                total = total.add(s.getTotalAmount());
            }
        }
        return total;
    }

    public BigDecimal calculateTodaySales() {
        java.time.LocalDate today = java.time.LocalDate.now();
        BigDecimal total = BigDecimal.ZERO;
        for (Sale s : getAllSales()) {
            if (today.equals(s.getSaleDate()) && s.getTotalAmount() != null) {
                total = total.add(s.getTotalAmount());
            }
        }
        return total;
    }
}

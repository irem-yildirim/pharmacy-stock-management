package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.SaleItemDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.pattern.AppLogger;
import com.pharmacy.pattern.TransactionFactory;

import java.math.BigDecimal;
import java.util.List;

public class SaleService {

    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;
    private final DrugDAO drugDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public SaleService(SaleDAO saleDAO, SaleItemDAO saleItemDAO, DrugDAO drugDAO) {
        this.saleDAO = saleDAO;
        this.saleItemDAO = saleItemDAO;
        this.drugDAO = drugDAO;
    }

    public Sale createSale(List<SaleItem> items) {
        logger.log("Creating new sale with " + items.size() + " item(s)");

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

        Sale sale = TransactionFactory.createSale(total);
        saleDAO.save(sale);

        for (SaleItem item : items) {
            item.setSale(sale);
            saleItemDAO.save(item);
        }

        logger.log("Sale #" + sale.getId() + " created. Total: " + total);
        return sale;
    }

    public List<Sale> getAllSales() {
        logger.log("Fetching all sales");
        return saleDAO.findAll();
    }

    public Sale getSaleById(Long id) {
        logger.log("Fetching sale #" + id);
        Sale sale = saleDAO.findById(id);
        if (sale == null) {
            throw new IllegalArgumentException("Sale not found: " + id);
        }
        return sale;
    }

    public List<SaleItem> getAllSaleItems() {
        logger.log("Fetching full sale history (all SaleItems)");
        return saleItemDAO.findAll();
    }
}

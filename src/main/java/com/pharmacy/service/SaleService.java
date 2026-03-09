package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.SaleItemDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.pattern.AppLogger;
import com.pharmacy.pattern.TransactionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for Sale operations.
 * Uses {@link TransactionFactory} to create Sale objects (Factory Pattern).
 */
@Service
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

    /**
     * Creates a new Sale from a list of SaleItems.
     * Computes totalAmount, updates drug stock, and persists everything atomically.
     *
     * @param items list of sale line items (drug barcode + quantity + unitPrice
     *              expected)
     * @return the saved Sale
     */
    @Transactional
    public Sale createSale(List<SaleItem> items) {
        logger.log("Creating new sale with " + items.size() + " item(s)");

        // Compute total amount and deduct drug stock
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : items) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);

            Drug drug = item.getDrug();
            if (drug != null && drug.getBarcode() != null) {
                Drug persistedDrug = drugDAO.findById(drug.getBarcode())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Drug not found: " + drug.getBarcode()));
                persistedDrug.setStockQuantity(persistedDrug.getStockQuantity() - item.getQuantity());
                drugDAO.save(persistedDrug);
                item.setDrug(persistedDrug);
            }
        }

        // Use TransactionFactory — Factory Pattern
        Sale sale = TransactionFactory.createSale(total);
        Sale savedSale = saleDAO.save(sale);

        // Link items to the saved sale
        for (SaleItem item : items) {
            item.setSale(savedSale);
        }
        saleItemDAO.saveAll(items);

        logger.log("Sale #" + savedSale.getId() + " created. Total: " + total);
        return savedSale;
    }

    public List<Sale> getAllSales() {
        logger.log("Fetching all sales");
        return saleDAO.findAll();
    }

    public Sale getSaleById(Long id) {
        logger.log("Fetching sale #" + id);
        return saleDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found: " + id));
    }

    public List<SaleItem> getAllSaleItems() {
        logger.log("Fetching full sale history (all SaleItems)");
        return saleItemDAO.findAll();
    }
}

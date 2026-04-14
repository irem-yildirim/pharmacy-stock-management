package com.pharmacy.controllers;

import com.pharmacy.entity.*;
import com.pharmacy.service.SaleService;
import com.pharmacy.service.PurchaseService;
import com.pharmacy.service.DrugService;

import java.util.ArrayList;
import java.util.List;

/**
 * Alış (Purchase) ve Satış (Sell) işlemlerini koordine eder.
 * (Single Responsibility Principle)
 */
public class TransactionController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final DrugService drugService; 

    public TransactionController(SaleService saleService, PurchaseService purchaseService, DrugService drugService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.drugService = drugService;
    }

    public boolean sellDrug(String barcode, int quantity) {
        try {
            Drug d = drugService.findByBarcode(barcode);
            if (d == null || d.getStockQuantity() < quantity) return false;

            SaleItem item = new SaleItem();
            item.setDrug(d);
            item.setQuantity(quantity);
            item.setUnitPrice(d.getSellingPrice());

            List<SaleItem> items = new ArrayList<>();
            items.add(item);
            saleService.createSale(items);
            return true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw e; // İş kuralı hatalarını View katmanına ilet
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean purchaseDrug(String barcode, int quantity) {
        try {
            purchaseService.addPurchase(barcode, quantity);
            return true;
        } catch (IllegalArgumentException e) {
            throw e; // İş kuralı hatalarını View katmanına ilet
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

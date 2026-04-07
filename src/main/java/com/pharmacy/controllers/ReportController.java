package com.pharmacy.controllers;

import com.pharmacy.entity.*;
import com.pharmacy.service.SaleService;
import com.pharmacy.service.PurchaseService;
import com.pharmacy.service.DrugService;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Finansal analizleri, toplam stok raporlarını çıkartır. 
 * (Single Responsibility Principle)
 */
public class ReportController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final DrugService drugService;

    public ReportController(SaleService saleService, PurchaseService purchaseService, DrugService drugService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.drugService = drugService;
    }

    public static class FinancialTransaction {
        public final String type;
        public final LocalDate date;
        public final BigDecimal amount;
        public final String reference;

        public FinancialTransaction(String type, LocalDate date, BigDecimal amount, String reference) {
            this.type = type;
            this.date = date;
            this.amount = amount;
            this.reference = reference;
        }
    }

    public static class FinancialSummary {
        public final BigDecimal totalSales;
        public final BigDecimal totalPurchases;
        public final BigDecimal netProfit;
        public final BigDecimal todayRevenue;
        public final int lowStockCount;
        public final int expiryCount;
        public final int totalInventory;

        public FinancialSummary(BigDecimal totalSales, BigDecimal totalPurchases,
                BigDecimal todayRevenue, int lowStockCount, int expiryCount, int totalInventory) {
            this.totalSales = totalSales != null ? totalSales : BigDecimal.ZERO;
            this.totalPurchases = totalPurchases != null ? totalPurchases : BigDecimal.ZERO;
            this.netProfit = this.totalSales.subtract(this.totalPurchases);
            this.todayRevenue = todayRevenue != null ? todayRevenue : BigDecimal.ZERO;
            this.lowStockCount = lowStockCount;
            this.expiryCount = expiryCount;
            this.totalInventory = totalInventory;
        }
    }

    public FinancialSummary getFinancialSummary() {
        List<Drug> all = drugService.getAllDrugs();
        int totalInv = all.size();
        int lowStock = 0;
        int expCount = 0;
        LocalDate threshold30 = LocalDate.now().plusDays(30);

        for (Drug m : all) {
            if (m.getStockQuantity() < 10) {
                lowStock++;
            }
            Expiry exp = m.getExpiry();
            if (exp != null && exp.getExpirationDate() != null && !exp.getExpirationDate().isAfter(threshold30)) {
                expCount++;
            }
        }

        return new FinancialSummary(
                saleService.calculateTotalSales(),
                purchaseService.calculateTotalPurchases(),
                saleService.calculateTodaySales(),
                lowStock,
                expCount,
                totalInv);
    }

    public List<FinancialTransaction> getFinancialTransactions() {
        List<FinancialTransaction> list = new ArrayList<>();

        for (Sale s : saleService.getAllSales()) {
            list.add(new FinancialTransaction("SALE", s.getSaleDate(), s.getTotalAmount(), "Sale ID #" + s.getId()));
        }

        for (Purchase p : purchaseService.getAllPurchases()) {
            BigDecimal amount = BigDecimal.ZERO;
            if (p.getDrug() != null && p.getDrug().getBarcode() != null) {
                Drug drug = drugService.findByBarcode(p.getDrug().getBarcode());
                if (drug != null && drug.getCostPrice() != null) {
                    amount = drug.getCostPrice().multiply(BigDecimal.valueOf(p.getQuantityAdded()));
                }
            }
            list.add(new FinancialTransaction("PURCHASE", p.getPurchaseDate(), amount,
                    "Purchase: " + (p.getDrug() != null ? p.getDrug().getBarcode() : "Unknown")));
        }

        list.sort((a, b) -> {
            if (a.date == null && b.date == null) return 0;
            if (a.date == null) return 1;
            if (b.date == null) return -1;
            return b.date.compareTo(a.date);
        });

        return list;
    }
}

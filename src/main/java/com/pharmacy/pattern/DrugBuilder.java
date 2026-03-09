package com.pharmacy.pattern;

import com.pharmacy.entity.Drug;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Implements the <b>Builder Pattern</b> for the {@link Drug} entity.
 *
 * <p>
 * <b>Problem solved:</b> The Drug class has 9+ fields. A telescoping
 * constructor
 * approach would require many overloaded constructors, making the code
 * unreadable
 * and error-prone (e.g., confusing costPrice with sellingPrice positionally).
 * </p>
 *
 * <p>
 * <b>Solution:</b> The Builder allows constructing a Drug object step-by-step
 * using named setter-style methods, ensuring clarity and preventing mistakes.
 * </p>
 *
 * <p>
 * <b>Usage example:</b>
 * 
 * <pre>
 * Drug aspirin = new DrugBuilder()
 *         .barcode("8699514016444")
 *         .name("Aspirin 500mg")
 *         .type("Analgesic")
 *         .dose("500mg")
 *         .costPrice(new BigDecimal("2.50"))
 *         .sellingPrice(new BigDecimal("5.00"))
 *         .stockQuantity(100)
 *         .productionDate(LocalDate.of(2024, 1, 1))
 *         .expirationDate(LocalDate.of(2026, 12, 31))
 *         .build();
 * </pre>
 * </p>
 *
 * @see Drug
 */
public class DrugBuilder {

    // ── Builder fields (mirroring Drug fields) ───────────────────────────────
    private String barcode;
    private String name;
    private String type;
    private String dose;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private com.pharmacy.entity.Category category;
    private String prescriptionType;

    // ── Fluent setter methods ────────────────────────────────────────────────

    public DrugBuilder barcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public DrugBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DrugBuilder type(String type) {
        this.type = type;
        return this;
    }

    public DrugBuilder dose(String dose) {
        this.dose = dose;
        return this;
    }

    public DrugBuilder costPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public DrugBuilder sellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
        return this;
    }

    public DrugBuilder stockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        return this;
    }

    public DrugBuilder productionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
        return this;
    }

    public DrugBuilder expirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public DrugBuilder category(com.pharmacy.entity.Category category) {
        this.category = category;
        return this;
    }

    public DrugBuilder prescriptionType(String prescriptionType) {
        this.prescriptionType = prescriptionType;
        return this;
    }

    // ── Build ────────────────────────────────────────────────────────────────

    /**
     * Constructs and returns a fully populated {@link Drug} instance.
     *
     * @throws IllegalStateException if required fields (barcode, name, costPrice,
     *                               sellingPrice, expirationDate) are missing.
     */
    public Drug build() {
        if (barcode == null || barcode.isBlank()) {
            throw new IllegalStateException("DrugBuilder: barcode is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("DrugBuilder: name is required");
        }
        if (costPrice == null) {
            throw new IllegalStateException("DrugBuilder: costPrice is required");
        }
        if (sellingPrice == null) {
            throw new IllegalStateException("DrugBuilder: sellingPrice is required");
        }
        if (expirationDate == null) {
            throw new IllegalStateException("DrugBuilder: expirationDate is required");
        }

        Drug drug = new Drug();
        drug.setBarcode(barcode);
        drug.setName(name);
        drug.setType(type);
        drug.setDose(dose);
        drug.setCostPrice(costPrice);
        drug.setSellingPrice(sellingPrice);
        drug.setStockQuantity(stockQuantity);
        drug.setProductionDate(productionDate);
        drug.setExpirationDate(expirationDate);
        drug.setCategory(category);
        drug.setPrescriptionType(prescriptionType);
        return drug;
    }
}

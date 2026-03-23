package com.pharmacy.pattern;

import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Category;
import java.math.BigDecimal;

public class DrugBuilder {

    private String barcode;
    private String name;
    private String dose;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private Category category;

    public DrugBuilder barcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public DrugBuilder name(String name) {
        this.name = name;
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

    public DrugBuilder category(Category category) {
        this.category = category;
        return this;
    }

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

        Drug drug = new Drug();
        drug.setBarcode(barcode);
        drug.setName(name);
        drug.setDose(dose);
        drug.setCostPrice(costPrice);
        drug.setSellingPrice(sellingPrice);
        drug.setStockQuantity(stockQuantity);
        drug.setCategory(category);
        return drug;
    }
}

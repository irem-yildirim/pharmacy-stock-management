package com.pharmacy.entity;

import java.math.BigDecimal;

public class DrugBuilder {

    private String barcode;
    private String name;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private Category category;
    private Brand brand;
    private PresType presType;
    private Expiry expiry;

    public DrugBuilder setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public DrugBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public DrugBuilder setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public DrugBuilder setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
        return this;
    }

    public DrugBuilder setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        return this;
    }

    public DrugBuilder setCategory(Category category) {
        this.category = category;
        return this;
    }

    public DrugBuilder setBrand(Brand brand) {
        this.brand = brand;
        return this;
    }

    public DrugBuilder setPresType(PresType presType) {
        this.presType = presType;
        return this;
    }

    public DrugBuilder setExpiry(Expiry expiry) {
        this.expiry = expiry;
        return this;
    }

    /**
     * Tüm alanları DrugFactory üzerinden doğrular ve Drug nesnesini oluşturur.
     * Opsiyonel expiry alanı sonradan atanır.
     *
     * @throws IllegalArgumentException barkod veya isim boşsa
     */
    public Drug build() {
        Drug drug = DrugFactory.createDrug(barcode, name, costPrice, sellingPrice,
                stockQuantity, category, brand, presType);
        if (expiry != null) {
            drug.setExpiry(expiry);
        }
        return drug;
    }
}

package com.pharmacy.models;

import java.time.LocalDate;

public class Medicine {
    private int medId;
    private String medName;
    private String dose;
    private double cost;
    private double price;
    private int quantity;
    private int catId;
    private int brandId;
    private int supplierId;
    private int presId;
    private LocalDate expirationDate;

    public Medicine() {}

    private Medicine(Builder builder) {
        this.medId = builder.medId;
        this.medName = builder.medName;
        this.dose = builder.dose;
        this.cost = builder.cost;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.catId = builder.catId;
        this.brandId = builder.brandId;
        this.supplierId = builder.supplierId;
        this.presId = builder.presId;
        this.expirationDate = builder.expirationDate;
    }

    public static class Builder {
        private int medId;
        private String medName;
        private String dose;
        private double cost;
        private double price;
        private int quantity;
        private int catId;
        private int brandId;
        private int supplierId;
        private int presId;
        private LocalDate expirationDate;

        public Builder medId(int id) { this.medId = id; return this; }
        public Builder medName(String name) { this.medName = name; return this; }
        public Builder dose(String dose) { this.dose = dose; return this; }
        public Builder cost(double cost) { this.cost = cost; return this; }
        public Builder price(double price) { this.price = price; return this; }
        public Builder quantity(int qty) { this.quantity = qty; return this; }
        public Builder catId(int id) { this.catId = id; return this; }
        public Builder brandId(int id) { this.brandId = id; return this; }
        public Builder presId(int id) { this.presId = id; return this; }
        public Builder expirationDate(LocalDate date) { this.expirationDate = date; return this; }

        public Medicine build() { return new Medicine(this); }
    }

    public int getMedId() {
        return medId;
    }

    public void setMedId(int medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getPresId() {
        return presId;
    }

    public void setPresId(int presId) {
        this.presId = presId;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return medName + " (" + dose + ")";
    }
}

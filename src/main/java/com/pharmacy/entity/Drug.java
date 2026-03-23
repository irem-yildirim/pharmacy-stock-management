package com.pharmacy.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Drug {
    private String barcode;
    private String name;
    private String type;
    private String dose;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private LocalDate productionDate;
    private LocalDate expirationDate;

    private List<Purchase> purchases;
    private List<SaleItem> saleItems;
    private Expiry expiry;
    private Category category;

    private String prescriptionType;

    public Drug() {}

    public Drug(String barcode, String name, String type, String dose,
            BigDecimal costPrice, BigDecimal sellingPrice, int stockQuantity,
            LocalDate productionDate, LocalDate expirationDate) {
        this.barcode = barcode;
        this.name = name;
        this.type = type;
        this.dose = dose;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.productionDate = productionDate;
        this.expirationDate = expirationDate;
    }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public LocalDate getProductionDate() { return productionDate; }
    public void setProductionDate(LocalDate productionDate) { this.productionDate = productionDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }
    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }
    public Expiry getExpiry() { return expiry; }
    public void setExpiry(Expiry expiry) { this.expiry = expiry; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getPrescriptionType() { return prescriptionType; }
    public void setPrescriptionType(String prescriptionType) { this.prescriptionType = prescriptionType; }

    @Override
    public String toString() {
        return "Drug{barcode='" + barcode + "', name='" + name + "', stock=" + stockQuantity + "}";
    }
}

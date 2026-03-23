package com.pharmacy.entity;

import java.math.BigDecimal;
import java.util.List;

public class Drug {
    private String barcode;
    private String name;
    private String dose;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;

    private List<Purchase> purchases;
    private List<SaleItem> saleItems;
    private Expiry expiry;
    private Category category;

    public Drug() {}

    public Drug(String barcode, String name, String dose,
            BigDecimal costPrice, BigDecimal sellingPrice, int stockQuantity) {
        this.barcode = barcode;
        this.name = name;
        this.dose = dose;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }
    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }
    public Expiry getExpiry() { return expiry; }
    public void setExpiry(Expiry expiry) { this.expiry = expiry; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    @Override
    public String toString() {
        return "Drug{barcode='" + barcode + "', name='" + name + "', stock=" + stockQuantity + "}";
    }
}

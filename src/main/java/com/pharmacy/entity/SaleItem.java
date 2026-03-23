package com.pharmacy.entity;

import java.math.BigDecimal;

public class SaleItem {
    private Long id;
    private Sale sale;
    private Drug drug;
    private int quantity;
    private BigDecimal unitPrice;

    public SaleItem() {}

    public SaleItem(Sale sale, Drug drug, int quantity, BigDecimal unitPrice) {
        this.sale = sale;
        this.drug = drug;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "SaleItem{id=" + id + ", qty=" + quantity + ", unitPrice=" + unitPrice + "}";
    }
}

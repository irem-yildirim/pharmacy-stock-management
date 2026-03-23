package com.pharmacy.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Sale {
    private Long id;
    private BigDecimal totalAmount;
    private LocalDate saleDate;
    private List<SaleItem> items;

    public Sale() {}

    public Sale(BigDecimal totalAmount, LocalDate saleDate) {
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Sale{id=" + id + ", total=" + totalAmount + ", date=" + saleDate + "}";
    }
}

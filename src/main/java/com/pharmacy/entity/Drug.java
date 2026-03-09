package com.pharmacy.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Drug entity — the central domain object of the Pharmacy Stock Management
 * System.
 * Represents a pharmaceutical product in inventory.
 *
 * Instantiated via {@link com.pharmacy.pattern.DrugBuilder} (Builder Pattern).
 */
@Entity
@Table(name = "drug")
public class Drug {

    @Id
    @Column(name = "barcode", nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "dose", length = 50)
    private String dose;

    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Purchase> purchases;

    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SaleItem> saleItems;

    @OneToOne(mappedBy = "drug", cascade = CascadeType.ALL)
    @JsonIgnore
    private Expiry expiry;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Drug() {
    }

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

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public Expiry getExpiry() {
        return expiry;
    }

    public void setExpiry(Expiry expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "Drug{barcode='" + barcode + "', name='" + name + "', stock=" + stockQuantity + "}";
    }
}

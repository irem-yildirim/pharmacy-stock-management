package com.pharmacy.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Sale entity — represents a completed sales transaction.
 * Created via
 * {@link com.pharmacy.pattern.TransactionFactory#createSale(BigDecimal)}
 * (Factory Pattern).
 */
@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleItem> items;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Sale() {
    }

    public Sale(BigDecimal totalAmount, LocalDate saleDate) {
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Sale{id=" + id + ", total=" + totalAmount + ", date=" + saleDate + "}";
    }
}

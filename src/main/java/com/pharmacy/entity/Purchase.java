package com.pharmacy.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Purchase entity — records stock additions for a given drug.
 * Created via
 * {@link com.pharmacy.pattern.TransactionFactory#createPurchase(Drug, int)}
 * (Factory Pattern).
 */
@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_barcode", nullable = false)
    private Drug drug;

    @Column(name = "quantity_added", nullable = false)
    private int quantityAdded;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Purchase() {
    }

    public Purchase(Drug drug, int quantityAdded, LocalDate purchaseDate) {
        this.drug = drug;
        this.quantityAdded = quantityAdded;
        this.purchaseDate = purchaseDate;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public int getQuantityAdded() {
        return quantityAdded;
    }

    public void setQuantityAdded(int quantityAdded) {
        this.quantityAdded = quantityAdded;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "Purchase{id=" + id + ", quantityAdded=" + quantityAdded + ", date=" + purchaseDate + "}";
    }
}

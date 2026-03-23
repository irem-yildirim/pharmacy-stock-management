package com.pharmacy.entity;

import java.time.LocalDate;

public class Purchase {
    private Long id;
    private Drug drug;
    private int quantityAdded;
    private LocalDate purchaseDate;

    public Purchase() {}

    public Purchase(Drug drug, int quantityAdded, LocalDate purchaseDate) {
        this.drug = drug;
        this.quantityAdded = quantityAdded;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }
    public int getQuantityAdded() { return quantityAdded; }
    public void setQuantityAdded(int quantityAdded) { this.quantityAdded = quantityAdded; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    @Override
    public String toString() {
        return "Purchase{id=" + id + ", quantityAdded=" + quantityAdded + ", date=" + purchaseDate + "}";
    }
}

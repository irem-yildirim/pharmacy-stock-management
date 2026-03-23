package com.pharmacy.entity;

import java.time.LocalDate;

public class Expiry {
    private Long id;
    private Drug drug;
    private LocalDate expirationDate;
    private long daysRemaining;
    private String status;

    public Expiry() {}

    public Expiry(Drug drug, LocalDate expirationDate, long daysRemaining, String status) {
        this.drug = drug;
        this.expirationDate = expirationDate;
        this.daysRemaining = daysRemaining;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Expiry{drugBarcode=" + (drug != null ? drug.getBarcode() : "null")
                + ", expDate=" + expirationDate + ", daysRemaining=" + daysRemaining + ", status='" + status + "'}";
    }
}

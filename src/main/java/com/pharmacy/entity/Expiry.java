package com.pharmacy.entity;

public class Expiry {
    private Long id;
    private Drug drug;
    private long daysRemaining;
    private String status;

    public Expiry() {}

    public Expiry(Drug drug, long daysRemaining, String status) {
        this.drug = drug;
        this.daysRemaining = daysRemaining;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }
    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Expiry{drugBarcode=" + (drug != null ? drug.getBarcode() : "null")
                + ", daysRemaining=" + daysRemaining + ", status='" + status + "'}";
    }
}

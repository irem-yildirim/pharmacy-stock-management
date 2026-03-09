package com.pharmacy.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Expiry entity — tracks the expiration status of each drug.
 * Status values: "OK" | "CRITICAL" (≤30 days) | "EXPIRED" (≤0 days).
 * Populated and refreshed by
 * {@link com.pharmacy.service.ExpiryService#refreshExpiry()}.
 */
@Entity
@Table(name = "expiry")
public class Expiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_barcode", nullable = false, unique = true)
    @JsonIgnore
    private Drug drug;

    @Column(name = "days_remaining", nullable = false)
    private long daysRemaining;

    /** Status values: "OK", "CRITICAL", "EXPIRED" */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Expiry() {
    }

    public Expiry(Drug drug, long daysRemaining, String status) {
        this.drug = drug;
        this.daysRemaining = daysRemaining;
        this.status = status;
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

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Expiry{drugBarcode=" + (drug != null ? drug.getBarcode() : "null")
                + ", daysRemaining=" + daysRemaining + ", status='" + status + "'}";
    }
}

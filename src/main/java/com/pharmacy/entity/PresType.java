package com.pharmacy.entity;

public class PresType {
    private int presId;
    private String prescription;
    private int riskLevel;

    public PresType() {
    }

    public PresType(int presId, String prescription, int riskLevel) {
        this.presId = presId;
        this.prescription = prescription;
        this.riskLevel = riskLevel;
    }

    public int getPresId() {
        return presId;
    }

    public void setPresId(int presId) {
        this.presId = presId;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(int riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    public String toString() {
        return prescription;
    }
}

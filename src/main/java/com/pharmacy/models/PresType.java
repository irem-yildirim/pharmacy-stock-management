package com.pharmacy.models;

public class PresType {
    private int presId;
    private String prescription;
    private int level;

    public PresType() {
    }

    public PresType(int presId, String prescription, int level) {
        this.presId = presId;
        this.prescription = prescription;
        this.level = level;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return prescription;
    }
}

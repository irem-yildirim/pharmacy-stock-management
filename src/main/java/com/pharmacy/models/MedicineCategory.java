package com.pharmacy.models;

public class MedicineCategory {
    private int catId;
    private String catName;
    private String description;

    public MedicineCategory() {
    }

    public MedicineCategory(int catId, String catName, String description) {
        this.catId = catId;
        this.catName = catName;
        this.description = description;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return catName;
    }
}

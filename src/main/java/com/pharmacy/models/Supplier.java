package com.pharmacy.models;

public class Supplier {
    private int supplierId;
    private String supplierName;
    private String phoneNumber;

    public Supplier() {
    }

    public Supplier(int supplierId, String supplierName, String phoneNumber) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.phoneNumber = phoneNumber;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return supplierName;
    }
}

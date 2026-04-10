package com.pharmacy.entity;

import java.math.BigDecimal;

public class DrugFactory {

    // Yalnızca static metod içerdiği için instantiation'ı engelliyoruz
    private DrugFactory() {
    }

    /**
     * Zorunlu alanları doğrulayıp yeni bir Drug nesnesi oluşturur.
     *
     * @throws IllegalArgumentException barkod veya isim boşsa fırlatılır
     */
    public static Drug createDrug(String barcode,
            String name,
            BigDecimal costPrice,
            BigDecimal sellingPrice,
            int stockQuantity,
            Category category,
            Brand brand,
            PresType presType) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be empty.");
        }

        Drug drug = new Drug();
        drug.setBarcode(barcode.trim());
        drug.setName(name.trim());
        drug.setCostPrice(costPrice);
        drug.setSellingPrice(sellingPrice);
        drug.setStockQuantity(stockQuantity);
        drug.setCategory(category);
        drug.setBrand(brand);
        drug.setPresType(presType);
        return drug;
    }
}

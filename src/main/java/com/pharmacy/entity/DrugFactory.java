package com.pharmacy.entity;

import java.math.BigDecimal;

/**
 * Fabrika (Factory) Tasarım Deseni: İlaç nesnelerini tek merkezden üretiyor.
 * Bu sayede her yerden "new Drug()" yapmak yerine kuralları uygulayarak üretiyoruz.
 */
public class DrugFactory {

    // Yalnızca static metod içerdiği için bu sınıftan nesne oluşturulmamalı
    private DrugFactory() {}

    /**
     * Zorunlu alanları doğrulayıp yeni bir Drug nesnesi oluşturur ve döndürür.
     * Hatalı veri gelirse Exception fırlatarak üst katmanı uyarır.
     */
    public static Drug createDrug(String barcode, String name,
            BigDecimal costPrice, BigDecimal sellingPrice,
            int stockQuantity, Category category, Brand brand, PresType presType) {

        // Temel validasyon kontrolleri — boş veya geçersiz değerler reddedilir
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be empty.");
        }
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost price cannot be null or negative.");
        }
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Selling price must be greater than zero.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }

        // Kontollerden geçtikten sonra ilaç nesnesini oluştur ve döndür
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

package com.pharmacy.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sistemdeki her bir ilacı temsil eden ana veri sınıfı (Entity).
 * Veritabanındaki 'drug' tablosunun Java karşılığı.
 */
public class Drug {
    // İlacın birincil anahtarı (Primary Key) — 8 haneli olması zorunlu
    private String barcode;
    private String name;
    private BigDecimal costPrice;    // Eczanenin ödediği alış fiyatı
    private BigDecimal sellingPrice; // Müşteriye satılan fiyat — her zaman costPrice'tan yüksek olmalı
    private int stockQuantity;

    // İlacın bağlı olduğu diğer tablolardan gelen nesneler (ilişkili veriler)
    private List<Purchase> purchases;
    private List<SaleItem> saleItems;
    private Expiry expiry;      // Son kullanma tarihi bilgisi
    private Category category;  // Hangi kategoride (Antibiyotik, Ağrı Kesici vb.)
    private Brand brand;        // Hangi marka tarafından üretilmiş
    private PresType presType;  // Hangi reçete tipine ait (Beyaz, Yeşil, Kırmızı vb.)

    public Drug() {}

    // Hızlı nesne oluşturmak için temel alanları alan constructor
    public Drug(String barcode, String name,
            BigDecimal costPrice, BigDecimal sellingPrice, int stockQuantity) {
        this.barcode = barcode;
        this.name = name;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    // Getter ve Setter metodlar — her alan için okuma/yazma erişimi
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }
    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }
    public Expiry getExpiry() { return expiry; }
    public void setExpiry(Expiry expiry) { this.expiry = expiry; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }
    public PresType getPresType() { return presType; }
    public void setPresType(PresType presType) { this.presType = presType; }

    // Log çıktıları ve debug için okunabilir özet metin
    @Override
    public String toString() {
        return "Drug{barcode='" + barcode + "', name='" + name + "', stock=" + stockQuantity + "}";
    }
}

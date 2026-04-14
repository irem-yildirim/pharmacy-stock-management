package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.SaleItemDAO;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;
import java.math.BigDecimal;
import java.util.List;

/**
 * Satış (Sale) işlemlerinin iş kurallarını barındıran servis sınıfı.
 * Stokları güncelleyip, sepet tutarlarını hesaplayarak satışın veritabanına kaydedilmesini sağlar.
 */
public class SaleService {

    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;
    // Stok düşürme işlemi için ilaç bilgisine ihtiyaç var, o yüzden drugDAO da burada
    private final DrugDAO drugDAO;

    public SaleService(SaleDAO saleDAO, SaleItemDAO saleItemDAO, DrugDAO drugDAO) {
        this.saleDAO = saleDAO;
        this.saleItemDAO = saleItemDAO;
        this.drugDAO = drugDAO;
    }

    /**
     * Kullanıcının sepetindeki ürünler için yeni bir satış işlemi başlatır.
     * Stokları denetler, paraları hesaplar ve veritabanı işlemlerini sırasıyla yürütür.
     */
    public Sale createSale(List<SaleItem> items) {

        BigDecimal total = BigDecimal.ZERO;
        
        // Sepetteki (items) her bir ürün için tek tek dönüyoruz
        for (SaleItem item : items) {
            // İlgili satırdaki ürünün toplam fiyatı: Adet x Birim Fiyat
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal); // Tüm fiş tutarını biriktiriyoruz

            Drug drug = item.getDrug();
            if (drug != null && drug.getBarcode() != null) {
                // Güvenlik: Satışı yapılacak ilacın veritabanındaki en güncel halini çağır
                Drug persistedDrug = drugDAO.findById(drug.getBarcode());
                if (persistedDrug == null) {
                    // İlaç veritabanında yoksa satışı durdur
                    throw new IllegalArgumentException("Drug not found: " + drug.getBarcode());
                }
                
                // Defansif Kontrol: Stok yeterli mi diye bir son double-check (çifte kontrol) yapılması gerekir
                if (persistedDrug.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalStateException(
                        "Error: Insufficient stock! Medicine: " + persistedDrug.getName() +
                        " (Available: " + persistedDrug.getStockQuantity() +
                        ", Requested: " + item.getQuantity() + ")"
                    );
                }
                
                // İlacı satmaya uygun olduğumuz belgelendiği an ana stoktan adedi düşürüyoruz
                persistedDrug.setStockQuantity(persistedDrug.getStockQuantity() - item.getQuantity());
                drugDAO.update(persistedDrug);
                
                // Sepetteki objeyi veritabanından çektiğimiz gerçek (güncellenmiş stoka sahip) obje ile değiştiriyoruz
                item.setDrug(persistedDrug);
            }
        }

        // Fişin kendisini oluşturma adımı (Satış Ana Kaydı)
        Sale sale = new Sale();
        sale.setTotalAmount(total);
        sale.setSaleDate(java.time.LocalDate.now()); // Satış tarihini bugüne sabitle
        saleDAO.save(sale);

        // Satışın içindeki detay eşyaları (Satış Alt Kayıtları/Fiş kalemleri) ayrı tabloya kaydet
        for (SaleItem item : items) {
            item.setSale(sale);
            saleItemDAO.save(item);
        }

        return sale;
    }

    // Tüm geçmiş satışları listeler — Finans raporu için kullanılıyor
    public List<Sale> getAllSales() {
        return saleDAO.findAll();
    }

    // Bugüne kadar yapılan tüm satışların toplam tutarını hesaplar
    public BigDecimal calculateTotalSales() {
        BigDecimal total = BigDecimal.ZERO;
        for (Sale s : getAllSales()) {
            if (s.getTotalAmount() != null) {
                total = total.add(s.getTotalAmount());
            }
        }
        return total;
    }

    // Sadece bugün yapılan satışların toplam tutarını hesaplar — Dashboard'daki "Revenue" kartı için
    public BigDecimal calculateTodaySales() {
        java.time.LocalDate today = java.time.LocalDate.now();
        BigDecimal total = BigDecimal.ZERO;
        for (Sale s : getAllSales()) {
            // Satışın tarihi bugünkü tarihle eşleşiyorsa toplamaya ekle
            if (today.equals(s.getSaleDate()) && s.getTotalAmount() != null) {
                total = total.add(s.getTotalAmount());
            }
        }
        return total;
    }
}

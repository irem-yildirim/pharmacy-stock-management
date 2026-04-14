package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;

import java.math.BigDecimal;
import java.util.List;

/**
 * İlaçların (Drug) veritabanı işlemlerini ve iş kurallarını (business logic) yöneten servis katmanı.
 * Ekleme, güncelleme, silme ve doğrulama gibi işlemleri merkezileştirir.
 */
public class DrugService {

    private final DrugDAO drugDAO;

    // Servis başlatıldığında Data Access Object (Veri Erişim Nesnesi) enjekte edilir
    public DrugService(DrugDAO drugDAO) {
        this.drugDAO = drugDAO;
    }

    /**
     * Yeni bir ilaç kaydetmek için kullanılır.
     * @param drug Kaydedilecek ilaç objesi
     * @return Kaydı tamamlanan ilaç objesi
     */
    public Drug addDrug(Drug drug) {
        // 1. Gelen ilacın verilerinin kurallara uyup uymadığını kontrol et (Validasyon)
        validateDrug(drug);
        
        // 2. Eğer aynı barkoda sahip başka bir ilaç sistemde kayıtlıysa işlemi durdur ve hata fırlat
        if (drugDAO.findById(drug.getBarcode()) != null) {
            throw new IllegalArgumentException("Error: This barcode (" + drug.getBarcode() + ") already exists in the system.");
        }
        
        // 3. Her şey uygunsa veritabanına kaydet
        drugDAO.save(drug);
        return drug;
    }

    /**
     * Sistemde var olan bir ilacın bilgilerini (fiyat, stok vb.) günceller.
     * @param drug Güncellenecek yeni verileri barındıran ilaç objesi
     */
    public Drug updateDrug(Drug drug) {
        // Güncelleme işleminden önce kurallara uygunluğu tekrar denetle
        validateDrug(drug);
        drugDAO.update(drug);
        return drug;
    }

    /**
     * Defansif Programlama: İlaç verilerinin geçerliliğini doğrular.
     * Negatif fiyat, negatif stok veya boş barkod gibi durumları engeller.
     */
    private void validateDrug(Drug drug) {
        // Barkod alanının hem boş girilmemesi hem de tam olarak 8 karakter olması zorunluluğu
        if (drug.getBarcode() == null || drug.getBarcode().trim().length() != 8) {
            throw new IllegalArgumentException("Error: Medicine barcode must be exactly 8 characters long.");
        }
        // İlacın ismi girilmeden geçilemez
        if (drug.getName() == null || drug.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Medicine name cannot be empty.");
        }
        // Eczaneye geliş (alış) fiyatı sıfırın altına düşemez (zararına bile bedavaya alınmaz)
        if (drug.getCostPrice() == null || drug.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Error: Cost price cannot be less than 0.");
        }
        // Satış fiyatı zorunlu olarak sıfırdan büyük olmalıdır
        if (drug.getSellingPrice() == null || drug.getSellingPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Error: Selling price must be greater than 0.");
        }
        // Ortada olmayan eksi (-) bir stok miktarı olamaz
        if (drug.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Error: Stock quantity cannot be negative.");
        }
    }

    /**
     * Girilen barkoda sahip ilacı ve ona bağlı olan son kullanma tarihi (expiry) kayıtlarını veritabanından tamamen siler.
     * @param barcode Silinecek ilacın eşsiz barkodu
     */
    public void deleteDrug(String barcode) {
        try {
            // Önce bağlı olan expiry (son kullanma tarihi) kaydını temizle ki SQL Foreign Key (Yabancı Anahtar) hatası vermesin
            new com.pharmacy.dao.ExpiryDAO().deleteByDrugBarcode(barcode);
            
            // Sonrasında ana ilaç tablosundaki kaydı sil
            drugDAO.delete(barcode);
        } catch (RuntimeException e) {
            // Eğer ilacın önceden yapılmış satışı veya alımı varsa, veritabanı silmeye izin vermeyecektir. Bunu yakalayıp kullanıcıya temiz bildir.
            throw new IllegalStateException("Error: This medication cannot be deleted as it has transaction history.");
        }
    }

    // Bütün ilaçları veritabanından çekip liste olarak döndürür
    public List<Drug> getAllDrugs() {
        return drugDAO.findAll();
    }

    // Sadece spesifik bir barkoda ait ilacı bulmak için kullanılır
    public Drug findByBarcode(String barcode) {
        return drugDAO.findById(barcode);
    }

}

package com.pharmacy;

import com.pharmacy.dao.DBConnection;
import com.pharmacy.dao.DatabaseSeeder;
import com.pharmacy.dao.UserDAO;
import com.pharmacy.dao.DrugDAO;
import com.pharmacy.dao.CategoryDAO;
import com.pharmacy.dao.ExpiryDAO;
import com.pharmacy.service.UserService;
import com.pharmacy.service.DrugService;
import com.pharmacy.service.CategoryService;
import com.pharmacy.controllers.MedicineController;
import com.pharmacy.controllers.LoginController;
import com.pharmacy.views.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Eczane Otomasyonu Başlatılıyor...");

                // 1. Veritabanı Bağlantı Testi (Singleton Pattern)
                if (DBConnection.getInstance().getConnection() != null) {
                    System.out.println("MySQL Bağlantısı Başarılı!");
                }

                // 1.5 Seed Data — boş tabloları otomatik doldur
                new DatabaseSeeder().seedIfEmpty();

                // 2. DAO Katmanı (Veri Erişim)
                UserDAO userDAO = new UserDAO();
                DrugDAO drugDAO = new DrugDAO();
                CategoryDAO categoryDAO = new CategoryDAO();
                ExpiryDAO expiryDAO = new ExpiryDAO();

                // 3. Service Katmanı (İş Mantığı)
                UserService userService = new UserService(userDAO);
                DrugService drugService = new DrugService(drugDAO);
                CategoryService categoryService = new CategoryService(categoryDAO);

                // 4. Controller Katmanı (MVC Köprüsü)
                MedicineController medicineController = new MedicineController(drugService, categoryService, expiryDAO);
                LoginController loginController = new LoginController(userService, medicineController);

                // 5. View Katmanı (Dependency Injection ile başlatma)
                LoginView loginView = new LoginView(loginController);
                loginView.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Sistem Başlatılamadı!\nMySQL Veritabanını kontrol edin.\nHata: " + e.getMessage(),
                        "Kritik Hata", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}

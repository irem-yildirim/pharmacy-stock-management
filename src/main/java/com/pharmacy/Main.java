package com.pharmacy;

import com.pharmacy.dao.*;
import com.pharmacy.service.*;
import com.pharmacy.controllers.*;
import com.pharmacy.views.LoginView;

import javax.swing.*;

/** Projenin giriş noktası. 
 * DAO, Servis ve UI katmanlarının bir araya gelip 
 * sistemin ayağa kalktığı yer burası. */
public class Main {
    public static void main(String[] args) {
        // Arayüzün kilitlenmesin diye 
        // Swingde (EDT) başlatıyoruz.
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("--- Pharmacy Management System Starting ---");

                //1.veritabanını kotnrol edip verileri yüklüyoruz.
                //Katmanlı mimarii için önce veriye erişim (DAO),
                //sonra iş mantığı (Service) katmanlarını kuruyoruz.
                if (DBConnection.getInstance().getConnection() != null) {
                    System.out.println("[Database] Connection established.");
                }
                new DatabaseSeeder().seedIfEmpty();

                //2.Repository Layer (DAOs)
                UserDAO userDAO = new UserDAO();
                DrugDAO drugDAO = new DrugDAO();
                CategoryDAO categoryDAO = new CategoryDAO();
                SaleDAO saleDAO = new SaleDAO();
                SaleItemDAO saleItemDAO = new SaleItemDAO();
                PurchaseDAO purchaseDAO = new PurchaseDAO();
                BrandDAO brandDAO = new BrandDAO();
                PresTypeDAO presTypeDAO = new PresTypeDAO();

                //3.Logic Layer (Services)
                UserService userService = new UserService(userDAO);
                DrugService drugService = new DrugService(drugDAO);
                CategoryService categoryService = new CategoryService(categoryDAO);
                SaleService saleService = new SaleService(saleDAO, saleItemDAO, drugDAO);
                PurchaseService purchaseService = new PurchaseService(purchaseDAO, drugDAO);

                //4.Coordination Layer (MVC Controllers)
                MedicineController medicineController = new MedicineController(
                    drugService, categoryService, saleService, purchaseService, brandDAO, presTypeDAO
                );
                LoginController loginController = new LoginController(userService, medicineController);

                //5.Interface Layer (Swing UI)
                LoginView loginFrame = new LoginView(loginController);
                loginFrame.setVisible(true);

            } catch (Exception e) {
                System.err.println("[Main] Critical system failure during startup!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize system.\nPlease verify MySQL configuration.\nError: " + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

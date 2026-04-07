package com.pharmacy;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.controllers.LoginController;
import com.pharmacy.dao.*;
import com.pharmacy.service.*;
import com.pharmacy.views.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Database (Auto-Seed)
                new DatabaseSeeder().seedIfEmpty();

                // 1) DAO Katmanı
                UserDAO userDAO = new UserDAO();
                DrugDAO drugDAO = new DrugDAO();
                BrandDAO brandDAO = new BrandDAO();
                CategoryDAO categoryDAO = new CategoryDAO();
                PresTypeDAO presTypeDAO = new PresTypeDAO();
                PurchaseDAO purchaseDAO = new PurchaseDAO();
                SaleDAO saleDAO = new SaleDAO();
                SaleItemDAO saleItemDAO = new SaleItemDAO();
                ExpiryDAO expiryDAO = new ExpiryDAO();

                // 2) Service Katmanı
                UserService userService = new UserService(userDAO);
                CategoryService categoryService = new CategoryService(categoryDAO);
                DrugService drugService = new DrugService(drugDAO);
                PurchaseService purchaseService = new PurchaseService(purchaseDAO, drugDAO);
                SaleService saleService = new SaleService(saleDAO, saleItemDAO, drugDAO);

                // 3) Controller Katmanı
                InventoryController inventoryController = new InventoryController(drugService, categoryService, brandDAO, presTypeDAO);
                TransactionController transactionController = new TransactionController(saleService, purchaseService, drugService);
                ReportController reportController = new ReportController(saleService, purchaseService, drugService);
                
                LoginController loginController = new LoginController(userService, inventoryController, transactionController, reportController);

                // 4) View Başlatma
                LoginView loginView = new LoginView(loginController);
                loginView.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

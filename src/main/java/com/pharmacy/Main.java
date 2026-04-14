package com.pharmacy;

import com.pharmacy.controllers.InventoryController;
import com.pharmacy.controllers.TransactionController;
import com.pharmacy.controllers.ReportController;
import com.pharmacy.controllers.LoginController;
import com.pharmacy.dao.*;
import com.pharmacy.service.*;
import com.pharmacy.views.LoginView;
import javax.swing.SwingUtilities;

// Uygulamanın başlangıç noktası. Buradan her şey başlıyor!
public class Main {
    public static void main(String[] args) {
        // Swing arayüzünü her zaman kendi thread'inde açmamız gerekiyor — Java kuralı bu.
        SwingUtilities.invokeLater(() -> {
            try {
                // Uygulama açılırken veritabanı boşsa örnek verileri otomatik ekle
                new DatabaseSeeder().seedIfEmpty();

                // 1) DAO Katmanı — Veritabanıyla doğrudan konuşan sınıflar
                UserDAO userDAO = new UserDAO();
                DrugDAO drugDAO = new DrugDAO();
                BrandDAO brandDAO = new BrandDAO();
                CategoryDAO categoryDAO = new CategoryDAO();
                PresTypeDAO presTypeDAO = new PresTypeDAO();
                PurchaseDAO purchaseDAO = new PurchaseDAO();
                SaleDAO saleDAO = new SaleDAO();
                SaleItemDAO saleItemDAO = new SaleItemDAO();

                // 2) Service Katmanı — İş kurallarını yöneten asıl akıl burada
                UserService userService = new UserService(userDAO);
                CategoryService categoryService = new CategoryService(categoryDAO);
                DrugService drugService = new DrugService(drugDAO);
                PurchaseService purchaseService = new PurchaseService(purchaseDAO, drugDAO);
                SaleService saleService = new SaleService(saleDAO, saleItemDAO, drugDAO);

                // 3) Controller Katmanı — UI ile Service arasındaki köprü, haberciler gibi
                InventoryController inventoryController = new InventoryController(drugService, categoryService, brandDAO, presTypeDAO);
                TransactionController transactionController = new TransactionController(saleService, purchaseService, drugService);
                ReportController reportController = new ReportController(saleService, purchaseService, drugService);
                
                LoginController loginController = new LoginController(userService, inventoryController, transactionController, reportController);

                // 4) View Başlatma — Kullanıcının gördüğü ekranı aç
                LoginView loginView = new LoginView(loginController);
                loginView.setVisible(true);
            } catch (Exception e) {
                // Başlangıçta ciddi bir sorun olursa burada yakalanır — program çökmesin diye
                e.printStackTrace();
            }
        });
    }
}

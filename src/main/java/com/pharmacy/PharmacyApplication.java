package com.pharmacy;

import com.pharmacy.dao.*;
import com.pharmacy.service.*;
import com.pharmacy.pattern.AppLogger;

public class PharmacyApplication {

    public static void main(String[] args) {
        AppLogger logger = AppLogger.getInstance();
        logger.log("Pharmacy Stock Management System (Pure Java SE Edition) is starting...");

        // 1. Initialize DB Connection
        logger.log("Connecting to the database...");
        DBConnection.getInstance().getConnection();
        logger.log("Database connection successful!");

        // 2. Initialize DAOs
        UserDAO userDAO = new UserDAO();
        DrugDAO drugDAO = new DrugDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        PurchaseDAO purchaseDAO = new PurchaseDAO();
        SaleDAO saleDAO = new SaleDAO();
        SaleItemDAO saleItemDAO = new SaleItemDAO();
        ExpiryDAO expiryDAO = new ExpiryDAO();

        // 3. Initialize Services (Dependency Injection via Constructor)
        UserService userService = new UserService(userDAO);
        DrugService drugService = new DrugService(drugDAO);
        CategoryService categoryService = new CategoryService(categoryDAO);
        PurchaseService purchaseService = new PurchaseService(purchaseDAO, drugDAO);
        SaleService saleService = new SaleService(saleDAO, saleItemDAO, drugDAO);
        ExpiryService expiryService = new ExpiryService(expiryDAO, drugDAO);

        logger.log("System initialization complete. Modules are ready.");

        // TODO: UI Launch Here
        // e.g., java.awt.EventQueue.invokeLater(() -> new
        // LoginFrame(userService).setVisible(true));

        logger.log("Application is running inside standard JVM.");
    }
}

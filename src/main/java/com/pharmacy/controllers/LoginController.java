package com.pharmacy.controllers;

import com.pharmacy.service.UserService;
import com.pharmacy.entity.User;
import java.util.List;

/**
 * Kullanıcı kimlik doğrulamasını ve diğer controller referanslarını yönetir.
 */
public class LoginController {

    private final UserService userService;
    private final InventoryController inventoryController;
    private final TransactionController transactionController;
    private final ReportController reportController;

    public LoginController(UserService userService, InventoryController invC, TransactionController transC, ReportController repC) {
        this.userService = userService;
        this.inventoryController = invC;
        this.transactionController = transC;
        this.reportController = repC;
    }

    public boolean login(String username, String password) {
        try {
            return userService.authenticate(username, password);
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public InventoryController getInventoryController()   { return inventoryController; }
    public TransactionController getTransactionController() { return transactionController; }
    public ReportController getReportController()         { return reportController; }
}

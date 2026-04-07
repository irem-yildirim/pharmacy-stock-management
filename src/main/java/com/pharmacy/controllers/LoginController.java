package com.pharmacy.controllers;

import com.pharmacy.service.UserService;
import com.pharmacy.entity.User;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.function.Consumer;

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

    public void loginAsync(String username, String password, Consumer<Boolean> callback) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return userService.authenticate(username, password);
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    callback.accept(get());
                } catch (Exception e) {
                    callback.accept(false);
                }
            }
        }.execute();
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

    public InventoryController getInventoryController() { return inventoryController; }
    public TransactionController getTransactionController() { return transactionController; }
    public ReportController getReportController() { return reportController; }
}

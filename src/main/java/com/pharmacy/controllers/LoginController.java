package com.pharmacy.controllers;

import com.pharmacy.service.UserService;
import com.pharmacy.entity.User;
import java.util.List;

/**
 * Kullanıcı kimlik doğrulamasını ve diğer controller referanslarını yönetir.
 * Giriş işlemi burada halloluyor, kim giriş yaptıysa bilgisi burada tutuluyor.
 */
public class LoginController {

    private final UserService userService;
    // Diğer controllerları burada tutuyoruz ki login sonrası Dashboard'a kolayca aktaralım
    private final InventoryController inventoryController;
    private final TransactionController transactionController;
    private final ReportController reportController;
    // O an sisteme giriş yapmış kullanıcı bu değişkende duruyor
    private User currentUser;

    // Tüm bağımlılıkları constructor üzerinden alıyoruz — Dependency Injection deniyor buna
    public LoginController(UserService userService, InventoryController invC, TransactionController transC, ReportController repC) {
        this.userService = userService;
        this.inventoryController = invC;
        this.transactionController = transC;
        this.reportController = repC;
    }

    // Kullanıcı adı ve şifreyi doğruluyoruz; başarılıysa o kullanıcıyı currentUser'a alıyoruz
    public boolean login(String username, String password) {
        try {
            boolean isValid = userService.authenticate(username, password);
            if (isValid) {
                // Giriş başarılıysa kim olduğunu hafızaya al — rol kontrolü için lazım
                this.currentUser = userService.getUserByUsername(username);
            }
            return isValid;
        } catch (Exception e) {
            // Bir şeyler patlarsa 'false' dönüyoruz, ekran çökmüyor
            return false;
        }
    }

    // Sistemdeki tüm kullanıcıları getiriyoruz — Login ekranındaki dropdown için
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Diğer controller'lara erişim sağlayan getter metodlar — DashboardView bunları çekiyor
    public InventoryController getInventoryController()   { return inventoryController; }
    public TransactionController getTransactionController() { return transactionController; }
    public ReportController getReportController()         { return reportController; }
    
    // O an giriş yapmış kullanıcıyı döndürüyoruz — Rol bazlı erişim kontrolü için kritik!
    public User getCurrentUser() {
        return currentUser;
    }
}

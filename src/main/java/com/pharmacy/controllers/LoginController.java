package com.pharmacy.controllers;

import com.pharmacy.service.UserService;
import com.pharmacy.entity.User;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller class to handle Login view actions.
 * Integrates SwingWorker to prevent UI freezing.
 */
public class LoginController {

    private final UserService userService;
    private final MedicineController medicineController;

    public LoginController(UserService userService, MedicineController medicineController) {
        this.userService = userService;
        this.medicineController = medicineController;
    }

    // SwingWorker ile Asenkron Login
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

    // Senkron Login (boolean doğrudan döner)
    public boolean login(String username, String password) {
        try {
            return userService.authenticate(username, password);
        } catch (Exception e) {
            return false;
        }
    }

    // Backend entity.User listesini döner
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public MedicineController getMedicineController() {
        return medicineController;
    }
}

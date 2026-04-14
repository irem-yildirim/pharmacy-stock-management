package com.pharmacy.service;

import com.pharmacy.dao.UserDAO;
import com.pharmacy.entity.User;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    // saveUser and deleteUser removed since UI disabled

    /**
     * Kullanıcı doğrulaması (Düz Metin / Basit versiyon)
     */
    public boolean authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }

    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}

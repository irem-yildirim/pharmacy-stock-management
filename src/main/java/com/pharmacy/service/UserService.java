package com.pharmacy.service;

import com.pharmacy.dao.UserDAO;
import com.pharmacy.entity.User;

import java.util.List;

// Kullanıcı doğrulama ve sorgulama işlemlerini yöneten servis sınıfı
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Login ekranındaki dropdown için tüm kullanıcıları getiriyoruz
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    // Kullanıcı adı ve şifre doğrulaması — Login butonuna basılınca bu çağrılıyor
    public boolean authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            // Düz metin karşılaştırması — şifre eşleşirse true dön
            return user.getPassword().equals(password);
        }
        return false; // Kullanıcı bulunamazsa da false döner
    }

    // Giriş başarılıysa o kullanıcının tüm bilgilerini (isim, rol vb.) getiriyoruz
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}

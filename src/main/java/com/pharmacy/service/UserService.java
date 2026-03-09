package com.pharmacy.service;

import com.pharmacy.dao.UserDAO;
import com.pharmacy.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User saveUser(User user) {
        return userDAO.save(user);
    }

    public void deleteUser(Long id) {
        userDAO.deleteById(id);
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Plain text comparison as requested for this project scope
            return user.getPassword().equals(password);
        }
        return false;
    }

    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username).orElse(null);
    }
}

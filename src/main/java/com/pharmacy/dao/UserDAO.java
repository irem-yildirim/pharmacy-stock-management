package com.pharmacy.dao;

import com.pharmacy.entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Kullanıcı verilerini veritabanında yöneten DAO sınıfı
public class UserDAO implements BaseDAO<User, Long> {

    // UI üzerinden kullanıcı oluşturulması kasıtlı olarak kapatılmış — sadece Seeder ekliyor
    @Override
    public void save(User user) {
        throw new UnsupportedOperationException("User creation via UI is disabled.");
    }

    @Override
    public void update(User user) {
        throw new UnsupportedOperationException("User update via UI is disabled.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("User deletion via UI is disabled.");
    }

    // ID ile tek bir kullanıcı kaydı getiriyoruz
    @Override
    public User findById(Long id) {
        String query = "SELECT * FROM users WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Login ekranı için kullanıcı adına göre tek kullanıcı getiriyoruz
    public User findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Login ekranındaki dropdown için tüm kullanıcıları getiriyoruz
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // SQL sonucunu User nesnesine dönüştüren yardımcı metot
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }
}

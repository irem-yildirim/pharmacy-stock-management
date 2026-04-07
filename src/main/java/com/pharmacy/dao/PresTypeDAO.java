package com.pharmacy.dao;

import com.pharmacy.entity.PresType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresTypeDAO implements BaseDAO<PresType, Integer> {

    @Override
    public void save(PresType p) {
        String query = "INSERT INTO pres_type (name, level) VALUES (?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getPrescription());
            pstmt.setInt(2, p.getRiskLevel());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setPresId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(PresType p) {
        String query = "UPDATE pres_type SET name=?, level=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getPrescription());
            pstmt.setInt(2, p.getRiskLevel());
            pstmt.setInt(3, p.getPresId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM pres_type WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PresType findById(Integer id) {
        String query = "SELECT * FROM pres_type WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PresType(rs.getInt("id"), rs.getString("name"), rs.getInt("level"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<PresType> findAll() {
        List<PresType> list = new ArrayList<>();
        String query = "SELECT * FROM pres_type";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new PresType(rs.getInt("id"), rs.getString("name"), rs.getInt("level")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

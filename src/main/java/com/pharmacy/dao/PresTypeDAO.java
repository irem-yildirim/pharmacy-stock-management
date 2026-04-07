package com.pharmacy.dao;

import com.pharmacy.entity.PresType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresTypeDAO implements BaseDAO<PresType, Integer> {

    @Override
    public void save(PresType p) {
        throw new UnsupportedOperationException("Prescription Type creation via UI is disabled.");
    }

    @Override
    public void update(PresType p) {
        throw new UnsupportedOperationException("Prescription Type update via UI is disabled.");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Prescription Type deletion via UI is disabled.");
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

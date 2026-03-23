package com.pharmacy.dao;

import com.pharmacy.entity.Expiry;
import com.pharmacy.entity.Drug;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpiryDAO implements BaseDAO<Expiry, Long> {

    @Override
    public void save(Expiry expiry) {
        String query = "INSERT INTO expiry (drug_barcode, days_remaining, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, expiry.getDrug().getBarcode());
            pstmt.setLong(2, expiry.getDaysRemaining());
            pstmt.setString(3, expiry.getStatus());
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expiry.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Expiry expiry) {
        String query = "UPDATE expiry SET days_remaining=?, status=? WHERE drug_barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, expiry.getDaysRemaining());
            pstmt.setString(2, expiry.getStatus());
            pstmt.setString(3, expiry.getDrug().getBarcode());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM expiry WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteByDrugBarcode(String barcode) {
        String query = "DELETE FROM expiry WHERE drug_barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, barcode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Expiry findById(Long id) {
        String query = "SELECT * FROM expiry WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpiry(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Expiry findByDrugBarcode(String barcode) {
        String query = "SELECT * FROM expiry WHERE drug_barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpiry(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Expiry> findAll() {
        List<Expiry> list = new ArrayList<>();
        String query = "SELECT * FROM expiry";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToExpiry(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Expiry mapResultSetToExpiry(ResultSet rs) throws SQLException {
        Expiry e = new Expiry();
        e.setId(rs.getLong("id"));
        
        Drug drug = new Drug();
        drug.setBarcode(rs.getString("drug_barcode"));
        e.setDrug(drug);
        
        e.setDaysRemaining(rs.getLong("days_remaining"));
        e.setStatus(rs.getString("status"));
        return e;
    }
}

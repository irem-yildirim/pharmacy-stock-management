package com.pharmacy.dao;

import com.pharmacy.entity.Purchase;
import com.pharmacy.entity.Drug;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO implements BaseDAO<Purchase, Long> {

    @Override
    public void save(Purchase purchase) {
        String query = "INSERT INTO purchase (drug_barcode, quantity_added, purchase_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, purchase.getDrug().getBarcode());
            pstmt.setInt(2, purchase.getQuantityAdded());
            pstmt.setDate(3, purchase.getPurchaseDate() != null ? Date.valueOf(purchase.getPurchaseDate()) : null);
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    purchase.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Purchase purchase) {
        String query = "UPDATE purchase SET drug_barcode=?, quantity_added=?, purchase_date=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, purchase.getDrug().getBarcode());
            pstmt.setInt(2, purchase.getQuantityAdded());
            pstmt.setDate(3, purchase.getPurchaseDate() != null ? Date.valueOf(purchase.getPurchaseDate()) : null);
            pstmt.setLong(4, purchase.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM purchase WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Purchase findById(Long id) {
        String query = "SELECT * FROM purchase WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPurchase(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Purchase> findAll() {
        List<Purchase> purchases = new ArrayList<>();
        String query = "SELECT * FROM purchase";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                purchases.add(mapResultSetToPurchase(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    private Purchase mapResultSetToPurchase(ResultSet rs) throws SQLException {
        Purchase p = new Purchase();
        p.setId(rs.getLong("id"));
        
        Drug drug = new Drug();
        drug.setBarcode(rs.getString("drug_barcode"));
        p.setDrug(drug);
        
        p.setQuantityAdded(rs.getInt("quantity_added"));
        Date pDate = rs.getDate("purchase_date");
        if (pDate != null) p.setPurchaseDate(pDate.toLocalDate());
        
        return p;
    }
}

package com.pharmacy.dao;

import com.pharmacy.entity.Drug;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrugDAO implements BaseDAO<Drug, String> {

    @Override
    public void save(Drug drug) {
        String query = "INSERT INTO drug (barcode, name, type, dose, cost_price, selling_price, stock_quantity, production_date, expiration_date, prescription_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, drug.getBarcode());
            pstmt.setString(2, drug.getName());
            pstmt.setString(3, drug.getType());
            pstmt.setString(4, drug.getDose());
            pstmt.setBigDecimal(5, drug.getCostPrice());
            pstmt.setBigDecimal(6, drug.getSellingPrice());
            pstmt.setInt(7, drug.getStockQuantity());
            pstmt.setDate(8, drug.getProductionDate() != null ? Date.valueOf(drug.getProductionDate()) : null);
            pstmt.setDate(9, drug.getExpirationDate() != null ? Date.valueOf(drug.getExpirationDate()) : null);
            pstmt.setString(10, drug.getPrescriptionType());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Drug drug) {
        String query = "UPDATE drug SET name=?, type=?, dose=?, cost_price=?, selling_price=?, stock_quantity=?, production_date=?, expiration_date=?, prescription_type=? WHERE barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, drug.getName());
            pstmt.setString(2, drug.getType());
            pstmt.setString(3, drug.getDose());
            pstmt.setBigDecimal(4, drug.getCostPrice());
            pstmt.setBigDecimal(5, drug.getSellingPrice());
            pstmt.setInt(6, drug.getStockQuantity());
            pstmt.setDate(7, drug.getProductionDate() != null ? Date.valueOf(drug.getProductionDate()) : null);
            pstmt.setDate(8, drug.getExpirationDate() != null ? Date.valueOf(drug.getExpirationDate()) : null);
            pstmt.setString(9, drug.getPrescriptionType());
            pstmt.setString(10, drug.getBarcode());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String barcode) {
        String query = "DELETE FROM drug WHERE barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, barcode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Drug findById(String barcode) {
        String query = "SELECT * FROM drug WHERE barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDrug(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Drug> findAll() {
        List<Drug> drugs = new ArrayList<>();
        String query = "SELECT * FROM drug";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                drugs.add(mapResultSetToDrug(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drugs;
    }

    private Drug mapResultSetToDrug(ResultSet rs) throws SQLException {
        Drug drug = new Drug();
        drug.setBarcode(rs.getString("barcode"));
        drug.setName(rs.getString("name"));
        drug.setType(rs.getString("type"));
        drug.setDose(rs.getString("dose"));
        drug.setCostPrice(rs.getBigDecimal("cost_price"));
        drug.setSellingPrice(rs.getBigDecimal("selling_price"));
        drug.setStockQuantity(rs.getInt("stock_quantity"));
        
        Date prodDate = rs.getDate("production_date");
        if (prodDate != null) drug.setProductionDate(prodDate.toLocalDate());
        
        Date expDate = rs.getDate("expiration_date");
        if (expDate != null) drug.setExpirationDate(expDate.toLocalDate());
        
        drug.setPrescriptionType(rs.getString("prescription_type"));
        return drug;
    }
}

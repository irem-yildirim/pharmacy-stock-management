package com.pharmacy.dao;

import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrugDAO implements BaseDAO<Drug, String> {

    @Override
    public void save(Drug drug) {
        String query = "INSERT INTO drug (barcode, name, dose, cost_price, selling_price, stock_quantity, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, drug.getBarcode());
            pstmt.setString(2, drug.getName());
            pstmt.setString(3, drug.getDose());
            pstmt.setBigDecimal(4, drug.getCostPrice());
            pstmt.setBigDecimal(5, drug.getSellingPrice());
            pstmt.setInt(6, drug.getStockQuantity());
            
            if (drug.getCategory() != null && drug.getCategory().getId() != null) {
                pstmt.setLong(7, drug.getCategory().getId());
            } else {
                pstmt.setNull(7, Types.BIGINT);
            }
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Drug drug) {
        String query = "UPDATE drug SET name=?, dose=?, cost_price=?, selling_price=?, stock_quantity=?, category_id=? WHERE barcode=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, drug.getName());
            pstmt.setString(2, drug.getDose());
            pstmt.setBigDecimal(3, drug.getCostPrice());
            pstmt.setBigDecimal(4, drug.getSellingPrice());
            pstmt.setInt(5, drug.getStockQuantity());
            
            if (drug.getCategory() != null && drug.getCategory().getId() != null) {
                pstmt.setLong(6, drug.getCategory().getId());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }
            
            pstmt.setString(7, drug.getBarcode());
            
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
        String query = "SELECT d.*, c.name AS category_name, c.description AS category_description " +
                       "FROM drug d " +
                       "LEFT JOIN category c ON d.category_id = c.id " +
                       "WHERE d.barcode = ?";
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
        String query = "SELECT d.*, c.name AS category_name, c.description AS category_description " +
                       "FROM drug d " +
                       "LEFT JOIN category c ON d.category_id = c.id";
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
        drug.setDose(rs.getString("dose"));
        drug.setCostPrice(rs.getBigDecimal("cost_price"));
        drug.setSellingPrice(rs.getBigDecimal("selling_price"));
        drug.setStockQuantity(rs.getInt("stock_quantity"));
        
        long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            Category category = new Category();
            category.setId(categoryId);
            category.setName(rs.getString("category_name"));
            category.setDescription(rs.getString("category_description"));
            drug.setCategory(category);
        }
        
        return drug;
    }
}

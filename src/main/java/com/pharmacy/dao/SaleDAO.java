package com.pharmacy.dao;

import com.pharmacy.entity.Sale;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO implements BaseDAO<Sale, Long> {

    @Override
    public void save(Sale sale) {
        String query = "INSERT INTO sale (total_amount, sale_date) VALUES (?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setBigDecimal(1, sale.getTotalAmount());
            pstmt.setDate(2, sale.getSaleDate() != null ? Date.valueOf(sale.getSaleDate()) : null);
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sale.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Sale sale) {
        String query = "UPDATE sale SET total_amount=?, sale_date=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setBigDecimal(1, sale.getTotalAmount());
            pstmt.setDate(2, sale.getSaleDate() != null ? Date.valueOf(sale.getSaleDate()) : null);
            pstmt.setLong(3, sale.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM sale WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Sale findById(Long id) {
        String query = "SELECT * FROM sale WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSale(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT * FROM sale";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                sales.add(mapResultSetToSale(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    private Sale mapResultSetToSale(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setId(rs.getLong("id"));
        s.setTotalAmount(rs.getBigDecimal("total_amount"));
        Date sDate = rs.getDate("sale_date");
        if (sDate != null) s.setSaleDate(sDate.toLocalDate());
        return s;
    }
}

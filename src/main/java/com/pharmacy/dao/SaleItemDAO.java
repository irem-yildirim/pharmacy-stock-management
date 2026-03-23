package com.pharmacy.dao;

import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.Sale;
import com.pharmacy.entity.Drug;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleItemDAO implements BaseDAO<SaleItem, Long> {

    @Override
    public void save(SaleItem saleItem) {
        String query = "INSERT INTO sale_item (sale_id, drug_barcode, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, saleItem.getSale().getId());
            pstmt.setString(2, saleItem.getDrug().getBarcode());
            pstmt.setInt(3, saleItem.getQuantity());
            pstmt.setBigDecimal(4, saleItem.getUnitPrice());
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    saleItem.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(SaleItem saleItem) {
        String query = "UPDATE sale_item SET sale_id=?, drug_barcode=?, quantity=?, unit_price=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, saleItem.getSale().getId());
            pstmt.setString(2, saleItem.getDrug().getBarcode());
            pstmt.setInt(3, saleItem.getQuantity());
            pstmt.setBigDecimal(4, saleItem.getUnitPrice());
            pstmt.setLong(5, saleItem.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM sale_item WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SaleItem findById(Long id) {
        String query = "SELECT * FROM sale_item WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSaleItem(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SaleItem> findAll() {
        List<SaleItem> items = new ArrayList<>();
        String query = "SELECT * FROM sale_item";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapResultSetToSaleItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private SaleItem mapResultSetToSaleItem(ResultSet rs) throws SQLException {
        SaleItem item = new SaleItem();
        item.setId(rs.getLong("id"));
        
        Sale sale = new Sale();
        sale.setId(rs.getLong("sale_id"));
        item.setSale(sale);
        
        Drug drug = new Drug();
        drug.setBarcode(rs.getString("drug_barcode"));
        item.setDrug(drug);
        
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        return item;
    }
}

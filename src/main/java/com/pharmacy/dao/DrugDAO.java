package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import com.pharmacy.entity.Brand;
import com.pharmacy.entity.Category;
import com.pharmacy.entity.Drug;
import com.pharmacy.entity.PresType;

public class DrugDAO implements BaseDAO<Drug, String> {

    @Override
    public void save(Drug drug) {
        String query = "INSERT INTO drug (barcode, name, dose, cost_price, selling_price, stock_quantity, category_id, brand_id, pres_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            if (drug.getBrand() != null && drug.getBrand().getBrandId() != 0) {
                pstmt.setLong(8, drug.getBrand().getBrandId());
            } else {
                pstmt.setNull(8, Types.BIGINT);
            }

            if (drug.getPresType() != null && drug.getPresType().getPresId() != 0) {
                pstmt.setLong(9, drug.getPresType().getPresId());
            } else {
                pstmt.setNull(9, Types.BIGINT);
            }

            // 1. İlaç drug tablosuna eklenir
            pstmt.executeUpdate();

            // 2.Tarihi expiry tablosuna ekle
            if (drug.getExpiry() != null && drug.getExpiry().getExpirationDate() != null) {
                String expiryQuery = "INSERT INTO expiry (drug_barcode, expiration_date) VALUES (?, ?)";
                try (PreparedStatement pstmtExpiry = conn.prepareStatement(expiryQuery)) {
                    pstmtExpiry.setString(1, drug.getBarcode());
                    pstmtExpiry.setDate(2, java.sql.Date.valueOf(drug.getExpiry().getExpirationDate()));
                    pstmtExpiry.executeUpdate(); // Tarih veritabanına işleni
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Drug drug) {
        String query = "UPDATE drug SET name=?, dose=?, cost_price=?, selling_price=?, stock_quantity=?, category_id=?, brand_id=?, pres_id=? WHERE barcode=?";
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

            if (drug.getBrand() != null && drug.getBrand().getBrandId() != 0) {
                pstmt.setLong(7, drug.getBrand().getBrandId());
            } else {
                pstmt.setNull(7, Types.BIGINT);
            }

            if (drug.getPresType() != null && drug.getPresType().getPresId() != 0) {
                pstmt.setLong(8, drug.getPresType().getPresId());
            } else {
                pstmt.setNull(8, Types.BIGINT);
            }

            pstmt.setString(9, drug.getBarcode());

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
        String query = "SELECT d.*, c.name AS category_name, c.description AS category_description, " +
                "b.name AS brand_name, " +
                "p.name AS pres_name, p.level AS pres_level, " +
                "e.id AS expiry_id, e.expiration_date " +
                "FROM drug d " +
                "LEFT JOIN category c ON d.category_id = c.id " +
                "LEFT JOIN brand b ON d.brand_id = b.id " +
                "LEFT JOIN pres_type p ON d.pres_id = p.id " +
                "LEFT JOIN expiry e ON d.barcode = e.drug_barcode " +
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
        // Liste yerine Map kullanıyoruz ki aynı barkodluları otomatik elesin
        java.util.Map<String, Drug> drugMap = new java.util.LinkedHashMap<>();

        String query = "SELECT d.*, c.name AS category_name, c.description AS category_description, " +
                "b.name AS brand_name, " +
                "p.name AS pres_name, p.level AS pres_level, " +
                "e.id AS expiry_id, e.expiration_date " +
                "FROM drug d " +
                "LEFT JOIN category c ON d.category_id = c.id " +
                "LEFT JOIN brand b ON d.brand_id = b.id " +
                "LEFT JOIN pres_type p ON d.pres_id = p.id " +
                "LEFT JOIN expiry e ON d.barcode = e.drug_barcode";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String barcode = rs.getString("barcode");
                // Eğer bu barkodlu ilaç haritaya daha önce eklenmediyse ekle!
                if (!drugMap.containsKey(barcode)) {
                    drugMap.put(barcode, mapResultSetToDrug(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Map'teki tekil (unique) ilaçları List'e çevirip gönderiyoruz
        return new ArrayList<>(drugMap.values());
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

        long brandId = rs.getLong("brand_id");
        if (!rs.wasNull()) {
            drug.setBrand(new Brand((int) brandId, rs.getString("brand_name")));
        }

        long presId = rs.getLong("pres_id");
        if (!rs.wasNull()) {
            drug.setPresType(new PresType((int) presId, rs.getString("pres_name"), rs.getInt("pres_level")));
        }

        long expiryId = rs.getLong("expiry_id");
        if (!rs.wasNull()) {
            com.pharmacy.entity.Expiry expiry = new com.pharmacy.entity.Expiry();
            expiry.setId(expiryId);
            java.sql.Date sqlDate = rs.getDate("expiration_date");
            if (sqlDate != null) {
                java.time.LocalDate expDate = sqlDate.toLocalDate();
                expiry.setExpirationDate(expDate);

                // Dynamic Calculation
                long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), expDate);
                expiry.setDaysRemaining(days);

                String status = days <= 0 ? "EXPIRED" : (days <= 30 ? "CRITICAL" : "OK");
                expiry.setStatus(status);
            }
            drug.setExpiry(expiry);
        }

        return drug;
    }
}

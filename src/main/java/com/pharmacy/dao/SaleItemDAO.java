package com.pharmacy.dao;

import com.pharmacy.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO interface for {@link SaleItem} entity.
 */
@Repository
public interface SaleItemDAO extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySale_Id(Long saleId);

    List<SaleItem> findByDrug_Barcode(String barcode);
}

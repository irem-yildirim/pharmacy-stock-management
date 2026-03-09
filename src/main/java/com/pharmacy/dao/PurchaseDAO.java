package com.pharmacy.dao;

import com.pharmacy.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO interface for {@link Purchase} entity.
 */
@Repository
public interface PurchaseDAO extends JpaRepository<Purchase, Long> {

    List<Purchase> findByDrug_Barcode(String barcode);
}

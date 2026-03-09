package com.pharmacy.dao;

import com.pharmacy.entity.Expiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for {@link Expiry} entity.
 */
@Repository
public interface ExpiryDAO extends JpaRepository<Expiry, Long> {

    List<Expiry> findByStatus(String status);

    Optional<Expiry> findByDrug_Barcode(String barcode);
}

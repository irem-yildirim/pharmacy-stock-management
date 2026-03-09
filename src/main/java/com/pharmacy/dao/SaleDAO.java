package com.pharmacy.dao;

import com.pharmacy.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO interface for {@link Sale} entity.
 */
@Repository
public interface SaleDAO extends JpaRepository<Sale, Long> {

    List<Sale> findBySaleDateBetween(LocalDate start, LocalDate end);
}

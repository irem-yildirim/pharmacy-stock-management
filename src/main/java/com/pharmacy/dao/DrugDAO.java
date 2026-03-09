package com.pharmacy.dao;

import com.pharmacy.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO (Data Access Object) interface for {@link Drug} entity.
 * Extends JpaRepository to inherit standard CRUD operations.
 * Named with "DAO" suffix per course convention.
 */
@Repository
public interface DrugDAO extends JpaRepository<Drug, String> {

    /**
     * Find drugs by name (case-insensitive contains search).
     */
    List<Drug> findByNameContainingIgnoreCase(String name);

    /**
     * Find drugs whose expiration date is before the given date (expired or
     * expiring soon).
     */
    List<Drug> findByExpirationDateBefore(LocalDate date);

    /**
     * Find drugs whose expiration date is between two dates.
     */
    List<Drug> findByExpirationDateBetween(LocalDate from, LocalDate to);
}

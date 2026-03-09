package com.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Pharmacy Stock Management System
 * University Course Project — Spring Boot REST API Backend
 *
 * Architecture: Layered
 *   Swing UI → REST API → Controller → Service → DAO → MySQL
 *
 * Design Patterns Implemented:
 *   - Builder   : DrugBuilder (com.pharmacy.pattern.DrugBuilder)
 *   - Factory   : TransactionFactory (com.pharmacy.pattern.TransactionFactory)
 *   - Singleton : AppLogger (com.pharmacy.pattern.AppLogger)
 *   - DAO       : Spring Data JPA interfaces with DAO suffix
 */
@SpringBootApplication
public class PharmacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmacyApplication.class, args);
    }
}

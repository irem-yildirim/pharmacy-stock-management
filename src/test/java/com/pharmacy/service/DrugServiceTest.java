package com.pharmacy.service;

import com.pharmacy.dao.DrugDAO;
import com.pharmacy.entity.Drug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Week 7 JUNIT 5 tests for DrugService logic.
 */
public class DrugServiceTest {

    private DrugService drugService;
    private DrugDAO mockDrugDAO;

    @BeforeEach
    void setUp() {
        // Mock the DAO to prevent real DB hits during logic test
        mockDrugDAO = new DrugDAO() {
            @Override
            public void save(Drug drug) {
                // simulated save logic
            }

            @Override
            public List<Drug> findAll() {
                // simulate returning empty list for testing
                return Collections.emptyList();
            }
        };
        drugService = new DrugService(mockDrugDAO);
    }

    @Test
    void testAddDrug_ShouldNotThrowExceptions() {
        Drug drug = new Drug();
        drug.setBarcode("TEST12345");
        drug.setName("Aspirin Test");
        drug.setCostPrice(new BigDecimal("10.0"));
        drug.setSellingPrice(new BigDecimal("15.0"));
        drug.setStockQuantity(100);

        // Ensure business layer allows save properly
        assertDoesNotThrow(() -> drugService.addDrug(drug),
                "Drug service should add valid drug without errors.");
    }

    @Test
    void testGetAllDrugs_ShouldReturnList() {
        List<Drug> results = drugService.getAllDrugs();

        // Assert List structure correctly connects to DAO
        assertNotNull(results, "Drug list should never be null.");
        assertTrue(results.isEmpty(), "Mock DB returns empty initially.");
    }
}

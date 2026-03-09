package com.pharmacy.controller;

import com.pharmacy.entity.Sale;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Sale operations.
 * Accepts a list of SaleItems and delegates sale creation to
 * {@link SaleService}.
 */
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    @org.springframework.beans.factory.annotation.Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /** Creates a new sale from a list of line items. */
    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody List<SaleItem> items) {
        Sale saved = saleService.createSale(items);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    /** Returns all sale line items — used as sales history view. */
    @GetMapping("/history")
    public ResponseEntity<List<SaleItem>> getSaleHistory() {
        return ResponseEntity.ok(saleService.getAllSaleItems());
    }
}

package com.pharmacy.controller;

import com.pharmacy.entity.Purchase;
import com.pharmacy.service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Purchase (stock restock) operations.
 * Uses a simple DTO to avoid deserializing JPA entity directly.
 */
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /**
     * Records a new drug purchase and updates stock.
     * Body: { "barcode": "...", "quantity": 50 }
     */
    @PostMapping
    public ResponseEntity<Purchase> addPurchase(@RequestBody PurchaseRequest request) {
        Purchase saved = purchaseService.addPurchase(request.getBarcode(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases() {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }

    @GetMapping("/drug/{barcode}")
    public ResponseEntity<List<Purchase>> getPurchasesByDrug(@PathVariable String barcode) {
        return ResponseEntity.ok(purchaseService.getPurchasesByDrug(barcode));
    }

    // ── Inner DTO ─────────────────────────────────────────────────────────────

    /**
     * Request DTO for purchase creation — avoids exposing JPA entity directly in
     * API.
     */
    public static class PurchaseRequest {
        private String barcode;
        private int quantity;

        public PurchaseRequest() {
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}

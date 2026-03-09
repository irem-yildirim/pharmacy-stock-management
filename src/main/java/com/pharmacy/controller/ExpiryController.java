package com.pharmacy.controller;

import com.pharmacy.entity.Expiry;
import com.pharmacy.service.ExpiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Expiry operations.
 * Thin controller — all logic delegated to {@link ExpiryService}.
 */
@RestController
@RequestMapping("/api/expiry")
public class ExpiryController {

    private final ExpiryService expiryService;

    public ExpiryController(ExpiryService expiryService) {
        this.expiryService = expiryService;
    }

    /** Triggers full recalculation of expiry status for all drugs. */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshExpiry() {
        expiryService.refreshExpiry();
        return ResponseEntity.ok("Expiry records refreshed successfully.");
    }

    @GetMapping
    public ResponseEntity<List<Expiry>> getAllExpiry() {
        return ResponseEntity.ok(expiryService.getAllExpiry());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Expiry>> getExpiredDrugs() {
        return ResponseEntity.ok(expiryService.getExpiredDrugs());
    }

    @GetMapping("/critical")
    public ResponseEntity<List<Expiry>> getCriticalDrugs() {
        return ResponseEntity.ok(expiryService.getCriticalDrugs());
    }
}

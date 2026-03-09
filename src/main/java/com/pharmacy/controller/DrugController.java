package com.pharmacy.controller;

import com.pharmacy.entity.Drug;
import com.pharmacy.service.DrugService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Drug CRUD and search operations.
 * Thin controller — all business logic delegated to {@link DrugService}.
 */
@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping
    public ResponseEntity<List<Drug>> getAllDrugs() {
        return ResponseEntity.ok(drugService.getAllDrugs());
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<Drug> getDrugByBarcode(@PathVariable String barcode) {
        return drugService.findByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Drug> addDrug(@RequestBody Drug drug) {
        Drug saved = drugService.addDrug(drug);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{barcode}")
    public ResponseEntity<Drug> updateDrug(@PathVariable String barcode, @RequestBody Drug drug) {
        drug.setBarcode(barcode);
        return ResponseEntity.ok(drugService.updateDrug(drug));
    }

    @DeleteMapping("/{barcode}")
    public ResponseEntity<Void> deleteDrug(@PathVariable String barcode) {
        drugService.deleteDrug(barcode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Drug>> getExpiringSoon(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(drugService.findExpiringSoon(days));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Drug>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(drugService.searchByName(name));
    }
}

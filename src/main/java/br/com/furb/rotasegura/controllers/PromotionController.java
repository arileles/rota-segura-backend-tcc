package br.com.furb.rotasegura.controllers;

import br.com.furb.rotasegura.domain.records.PromotionRecord;
import br.com.furb.rotasegura.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/promotion")
@RestController
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<Page<PromotionRecord>> list(Pageable pageable) {
        return ResponseEntity.ok(promotionService.findAllPromotions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionRecord> get(@PathVariable UUID id) {
        return ResponseEntity.ok(promotionService.findPromotionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionRecord> create(@RequestBody PromotionRecord promotion) {
        return ResponseEntity.ok(promotionService.save(promotion));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionRecord> update(@PathVariable UUID id, @RequestBody PromotionRecord promotion) {
        return ResponseEntity.ok(promotionService.update(id, promotion));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        promotionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

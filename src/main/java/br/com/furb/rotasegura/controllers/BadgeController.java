package br.com.furb.rotasegura.controllers;

import br.com.furb.rotasegura.domain.records.BadgeRecord;
import br.com.furb.rotasegura.services.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/badge")
@RestController
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping
    public ResponseEntity<Page<BadgeRecord>> list(Pageable pageable) {
        return ResponseEntity.ok(badgeService.findAllBadges(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BadgeRecord> get(@PathVariable UUID id) {
        return ResponseEntity.ok(badgeService.findBadgeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BadgeRecord> create(@RequestBody BadgeRecord badge) {
        return ResponseEntity.ok(badgeService.save(badge));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BadgeRecord> update(@PathVariable UUID id, @RequestBody BadgeRecord badge) {
        return ResponseEntity.ok(badgeService.update(id, badge));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        badgeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

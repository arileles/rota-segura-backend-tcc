package br.com.furb.rotasegura.controllers;

import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceStatus;
import br.com.furb.rotasegura.domain.records.OccurrenceRecord;
import br.com.furb.rotasegura.domain.records.ReporterCountRecord;
import br.com.furb.rotasegura.services.OccurrenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/occurrence")
@RestController
public class OccurrenceController {

    @Autowired
    private OccurrenceService occurrenceService;

    @GetMapping
    public ResponseEntity<List<OccurrenceRecord>> list(@RequestParam(required = false) OccurrenceStatus status, @RequestParam(required = false) OccurrenceSeverity occurrenceSeverity) {
        return ResponseEntity.ok(occurrenceService.findAllOccurrences(status, occurrenceSeverity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OccurrenceRecord> get(@PathVariable UUID id) {
        return ResponseEntity.ok(occurrenceService.findOccurrenceById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OccurrenceRecord>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(occurrenceService.findOccurrencesByUserId(userId));
    }

    // Busca por texto (LIKE) em description ou address
    @GetMapping("/search")
    public ResponseEntity<List<OccurrenceRecord>> search(@RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(occurrenceService.searchOccurrences(q));
    }

    // Retorna os usuários que mais reportaram ocorrências (do maior para o menor)
    @GetMapping("/top-reporters")
    public ResponseEntity<List<ReporterCountRecord>> topReporters() {
        return ResponseEntity.ok(occurrenceService.findTopReporters());
    }

    // Endpoint para criar ocorrência
    @PostMapping
    public ResponseEntity<OccurrenceRecord> create(@RequestBody OccurrenceRecord body) throws IOException {
        if (body.reporterUser() != null) {
            return ResponseEntity.ok(occurrenceService.save(body));
        }
        throw new IOException("Invalid user data");
    }

    @PutMapping("/{id}")
    public ResponseEntity<OccurrenceRecord> update(@PathVariable UUID id, @RequestBody OccurrenceRecord occurrence) {
        return ResponseEntity.ok(occurrenceService.update(id, occurrence));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        occurrenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Novo endpoint PATCH para marcar a ocorrência como RESOLVED
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<OccurrenceRecord> resolveOccurrence(@PathVariable UUID id) {
        OccurrenceRecord updated = occurrenceService.updateStatus(id, OccurrenceStatus.RESOLVED);
        return ResponseEntity.ok(updated);
    }
}

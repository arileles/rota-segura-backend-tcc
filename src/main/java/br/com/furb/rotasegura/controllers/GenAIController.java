package br.com.furb.rotasegura.controllers;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.com.furb.rotasegura.domain.entities.Occurrence;
import br.com.furb.rotasegura.domain.records.OccurrenceRecord;
import br.com.furb.rotasegura.exception.ResourceNotFoundException;
import br.com.furb.rotasegura.repositories.OccurenceRepository;
import br.com.furb.rotasegura.services.GenAIService;
import br.com.furb.rotasegura.services.OccurrenceService;

@RestController
@RequestMapping("/v1/genai")
public class GenAIController {

    private GenAIService service;

    @Autowired
    private OccurrenceService occurrenceService;

    public GenAIController(GenAIService service) {
        this.service = service;
    }

    @GetMapping(path = "/occurrence", produces = "application/json")
    public ResponseEntity<OccurrenceRecord> analyze(@RequestParam UUID id) {
        service.analyze(id);
        return ResponseEntity.ok(occurrenceService.findOccurrenceById(id));
    }
}

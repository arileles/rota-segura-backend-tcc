package br.com.furb.rotasegura.services;

import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceStatus;
import br.com.furb.rotasegura.domain.records.OccurrenceRecord;
import br.com.furb.rotasegura.domain.records.ReporterCountRecord;
import org.springframework.data.domain.Page;

import java.util.UUID;
import java.util.List;

public interface OccurrenceService {

    List<OccurrenceRecord> findAllOccurrences(OccurrenceStatus status, OccurrenceSeverity occurrenceSeverity);

    OccurrenceRecord findOccurrenceById(UUID id);

    OccurrenceRecord save(OccurrenceRecord occurrence);

    OccurrenceRecord update(UUID id, OccurrenceRecord data);

    void delete(UUID id);

    List<OccurrenceRecord> findOccurrencesByUserId(UUID userId);

    // Busca por texto (LIKE) em campos relevantes (description, address)
    List<OccurrenceRecord> searchOccurrences(String query);

    // Retorna os top N usuários que mais reportaram ocorrências (ordem decrescente)
    List<ReporterCountRecord> findTopReporters();

    // Atualiza apenas o status da ocorrência
    OccurrenceRecord updateStatus(UUID id, OccurrenceStatus status);
}

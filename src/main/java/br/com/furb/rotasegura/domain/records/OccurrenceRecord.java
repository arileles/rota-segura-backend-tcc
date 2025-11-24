package br.com.furb.rotasegura.domain.records;

import java.time.LocalDateTime;
import java.util.UUID;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceStatus;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;

public record OccurrenceRecord(
    UUID id,
    OccurrenceType type,
    OccurrenceSeverity severity,
    OccurrenceStatus status,
    String description,
    LocalDateTime reportedAt,
    LocalDateTime resolvedAt,
    Boolean validated,
    String reporterUser,
    Double latitude,
    Double longitude,
    String address,
    OccurrenceSeverity aiSeverity,
    OccurrenceType aiType
) {
}

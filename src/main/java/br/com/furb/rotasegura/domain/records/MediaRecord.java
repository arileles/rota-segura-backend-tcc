package br.com.furb.rotasegura.domain.records;

import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;

import java.util.UUID;

public record MediaRecord (UUID id, OccurrenceType aiType,
         OccurrenceSeverity aiSeverity) {

}

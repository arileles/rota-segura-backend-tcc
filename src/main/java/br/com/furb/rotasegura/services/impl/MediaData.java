package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaData {

    private String mediaType;
    private byte[] imageData;
    private OccurrenceType aiType;
    private OccurrenceSeverity aiSeverity;
}

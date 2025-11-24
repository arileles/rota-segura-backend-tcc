package br.com.furb.rotasegura.services;

import java.util.List;
import java.util.UUID;
import br.com.furb.rotasegura.domain.entities.Occurrence;
import br.com.furb.rotasegura.domain.records.MediaRecord;
import br.com.furb.rotasegura.services.impl.MediaData;

public interface MediaService {

    List<MediaRecord> list(UUID occurrenceId);

    MediaRecord get(UUID id);

    MediaRecord create(UUID occurenceId, String mediaType, byte[] imageData);

    MediaData load(UUID id);

    MediaRecord update(UUID id, MediaRecord data);

    void delete(UUID id);

    Occurrence getOccurrence(UUID id);

}

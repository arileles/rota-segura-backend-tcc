package br.com.furb.rotasegura.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.furb.rotasegura.domain.entities.Media;
import br.com.furb.rotasegura.domain.entities.Occurrence;
import br.com.furb.rotasegura.domain.records.MediaRecord;
import br.com.furb.rotasegura.infra.exception.ServiceException;
import br.com.furb.rotasegura.repositories.MediaRepository;
import br.com.furb.rotasegura.repositories.OccurenceRepository;
import br.com.furb.rotasegura.services.MediaService;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private OccurenceRepository occurenceRepository;

    @Transactional(readOnly = true)
    public List<MediaRecord> list(UUID occurrenceId) {
        return mediaRepository.findByOccurrenceId(occurrenceId).stream().map(MediaServiceImpl::mapEntityToRecord).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MediaRecord get(UUID id) {
        return mediaRepository.findById(id)
                .map(MediaServiceImpl::mapEntityToRecord)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "não encontrado"));
    }

    @Transactional
    public MediaRecord create(UUID occurenceId, String mediaType, byte[] imageData) {
        Media media = new Media();
        media.setId(UUID.randomUUID());
        media.setMediaType(mediaType);
        media.setImageData(imageData);
        media.setOccurrence(occurenceRepository.findById(occurenceId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Ocorrência não encontrada")));
        return mapEntityToRecord(mediaRepository.save(media));
    }
   
    public MediaData load(UUID id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "não encontrado"));
        return new MediaData(media.getMediaType(), media.getImageData(), media.getAiType(), media.getAiSeverity());
    }

    @Transactional
    public MediaRecord update(UUID id, MediaRecord data) {
        var current = mediaRepository.findById(id);
        if (current.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "não encontrado");
        }
        // aplicar lógica
        return mapEntityToRecord(mediaRepository.save(current.get()));
    }

    @Transactional
    public void delete(UUID id) {
        if (!mediaRepository.existsById(id)) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "não encontrado");
        }
        mediaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Occurrence getOccurrence(UUID id) {
        return occurenceRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Ocorrência não encontrada"));
    }

    private static MediaRecord mapEntityToRecord(Media entity) {
        return new MediaRecord(entity.getId(), entity.getAiType(), entity.getAiSeverity());
    }

    private static Media mapRecordToEntity(MediaRecord record) {
        var entity = new Media();
        entity.setId(record.id());
        return entity;
    }
}

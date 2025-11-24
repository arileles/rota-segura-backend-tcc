package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.domain.entities.Badge;
import br.com.furb.rotasegura.domain.records.BadgeRecord;
import br.com.furb.rotasegura.domain.utils.Utils;
import br.com.furb.rotasegura.infra.exception.ServiceException;
import br.com.furb.rotasegura.repositories.BadgeRepository;
import br.com.furb.rotasegura.services.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BadgeServiceImpl implements BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BadgeRecord> findAllBadges(@NonNull Pageable pageable) {
        return badgeRepository.findAll(pageable)
                .map(this::mapEntityToRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public BadgeRecord findBadgeById(UUID id) {
        var badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "não encontrado"));
        return mapEntityToRecord(badge);
    }

    @Override
    @Transactional
    public BadgeRecord save(BadgeRecord badge) {
        final String rawName = Utils.safeTrim(badge.name());
        if (Utils.isBlank(rawName)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Nome da badge é obrigatório.");
        }

        final Integer minPoints = badge.minPoints();
        if (minPoints == null || minPoints < 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "minPoints deve ser um inteiro não negativo.");
        }

        final Integer position = badge.position();
        if (position == null || position < 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "position deve ser um inteiro não negativo.");
        }

        if (badgeRepository.existsByNameIgnoreCase(rawName)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Já existe uma badge com esse nome.");
        }
        if (badgeRepository.existsByPosition(position)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Já existe uma badge na mesma posição.");
        }

        var entity = mapRecordToEntity(badge);
        entity.setId(UUID.randomUUID());
        entity.setName(rawName.trim());
        if (entity.getIsActive() == null) entity.setIsActive(Boolean.TRUE);

        try {
            var saved = badgeRepository.save(entity);
            return mapEntityToRecord(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



    @Override
    @Transactional
    public BadgeRecord update(UUID id, BadgeRecord data) {
        var current = badgeRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "não encontrado"));

        current.setUpdatedAt(LocalDateTime.now());
        current.setName(data.name());
        current.setIsActive(data.isActive());
        current.setPosition(data.minPoints());

        var updated = badgeRepository.save(current);
        return mapEntityToRecord(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!badgeRepository.existsById(id)) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "não encontrado");
        }
        badgeRepository.deleteById(id);
    }

    private BadgeRecord mapEntityToRecord(Badge badge) {
        if (badge == null) return null;
        return new BadgeRecord(
                badge.getId(),
                badge.getName(),
                badge.getMinPoints(),
                badge.getPosition(),
                badge.getIsActive()
        );
    }

    private Badge mapRecordToEntity(BadgeRecord badgeRecord) {
        if (badgeRecord == null) return null;

        Badge badge = new Badge();
        badge.setId(badgeRecord.id());
        badge.setName(badgeRecord.name());
        badge.setMinPoints(badgeRecord.minPoints());
        badge.setPosition(badgeRecord.position());
        badge.setIsActive(badgeRecord.isActive());

        return badge;
    }

}

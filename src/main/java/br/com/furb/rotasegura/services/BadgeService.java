package br.com.furb.rotasegura.services;

import br.com.furb.rotasegura.domain.records.BadgeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface BadgeService {

    Page<BadgeRecord> findAllBadges(@NonNull Pageable pageable);

    BadgeRecord findBadgeById(UUID id);

    BadgeRecord save(BadgeRecord badge);

    BadgeRecord update(UUID id, BadgeRecord data);

    void delete(UUID id);
}

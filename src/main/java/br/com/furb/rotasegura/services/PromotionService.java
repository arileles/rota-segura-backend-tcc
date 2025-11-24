package br.com.furb.rotasegura.services;

import br.com.furb.rotasegura.domain.records.PromotionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PromotionService {

    Page<PromotionRecord> findAllPromotions(Pageable pageable);

    PromotionRecord findPromotionById(UUID id);

    PromotionRecord save(PromotionRecord promotion);

    PromotionRecord update(UUID id, PromotionRecord data);

    void delete(UUID id);
}

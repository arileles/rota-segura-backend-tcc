package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.domain.entities.Promotion;
import br.com.furb.rotasegura.domain.records.PromotionRecord;
import br.com.furb.rotasegura.domain.utils.Utils;
import br.com.furb.rotasegura.infra.exception.ServiceException;
import br.com.furb.rotasegura.repositories.PromotionRepository;
import br.com.furb.rotasegura.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionRecord> findAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable).map(this::mapEntityToRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionRecord findPromotionById(UUID id) {
        var promotion =  promotionRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "nao encontrado"));
        return mapEntityToRecord(promotion);
    }

    @Override
    @Transactional
    public PromotionRecord save(PromotionRecord promotion) {
        if (promotion == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Payload da promoção é obrigatório.");
        }

        final String name        = Utils.safeTrim(promotion.name());
        final String coupon      = Utils.safeTrim(promotion.couponCode());

        if (Utils.isBlank(name))   throw new ServiceException(HttpStatus.BAD_REQUEST, "Nome da promoção é obrigatório.");
        if (Utils.isBlank(coupon)) throw new ServiceException(HttpStatus.BAD_REQUEST, "Cupom é obrigatório.");
        if (promotion.requiredPoints() == null || promotion.requiredPoints() < 0) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "requiredPoints deve ser um inteiro não negativo.");
        }

        if (promotionRepository.existsByCouponCodeIgnoreCase(coupon)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Já existe uma promoção com esse cupom.");
        }

        if (promotionRepository.existsByNameIgnoreCase(name)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Já existe uma promoção com esse nome.");
        }

        var entity = mapRecordToEntity(promotion);
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setCouponCode(coupon);
        try {
            var saved = promotionRepository.save(entity);
            return mapEntityToRecord(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Override
    @Transactional
    public PromotionRecord update(UUID id, PromotionRecord data) {
        var current = promotionRepository.findById(id).orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "não encontrado"));
        current.setName(data.name());
        current.setCouponCode(data.couponCode());
        current.setRequiredPoints(data.requiredPoints());
        var saved = promotionRepository.save(current);
        return mapEntityToRecord(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!promotionRepository.existsById(id)) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "não encontrado");
        }
        promotionRepository.deleteById(id);
    }

    private PromotionRecord mapEntityToRecord(Promotion promotion) {
        return new PromotionRecord(promotion.getId(), promotion.getName(), promotion.getCouponCode(), promotion.getRequiredPoints());
    }

    private Promotion mapRecordToEntity(PromotionRecord promotionRecord) {
        return new Promotion(promotionRecord.id(), promotionRecord.name(), promotionRecord.couponCode(), promotionRecord.requiredPoints());
    }
}



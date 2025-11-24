package br.com.furb.rotasegura.domain.records;

import java.util.UUID;

public record PromotionRecord(UUID id, String name, String couponCode, Integer requiredPoints){}
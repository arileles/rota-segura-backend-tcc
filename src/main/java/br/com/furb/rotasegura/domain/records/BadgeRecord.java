package br.com.furb.rotasegura.domain.records;

import java.util.UUID;

public record BadgeRecord(UUID id, String name, Integer minPoints, Integer position, Boolean isActive) {
}

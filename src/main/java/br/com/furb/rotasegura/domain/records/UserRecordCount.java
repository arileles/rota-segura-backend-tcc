package br.com.furb.rotasegura.domain.records;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserRecordCount(UUID id, String name, String email, LocalDateTime createDate, Integer level) {
}

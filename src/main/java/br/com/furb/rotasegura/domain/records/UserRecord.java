package br.com.furb.rotasegura.domain.records;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserRecord(UUID id, String password, String name, String email, Set<RoleRecord> role, LocalDateTime createDate, LocalDate birthDate, String phone, Integer level) {
}

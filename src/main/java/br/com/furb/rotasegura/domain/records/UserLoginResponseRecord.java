package br.com.furb.rotasegura.domain.records;

import java.util.UUID;

public record UserLoginResponseRecord(UUID id, String token, Long expiresIn) {
}

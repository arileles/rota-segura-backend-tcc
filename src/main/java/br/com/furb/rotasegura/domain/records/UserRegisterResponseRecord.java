package br.com.furb.rotasegura.domain.records;

import java.util.UUID;

public record UserRegisterResponseRecord(UUID id, String name, String email) {
}

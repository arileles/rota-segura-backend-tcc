package br.com.furb.rotasegura.services;

import br.com.furb.rotasegura.domain.entities.Role;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleService {

    Optional<Set<Role>> findRolesByUserId(UUID userId);
}


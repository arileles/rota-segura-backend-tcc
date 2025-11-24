package br.com.furb.rotasegura.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.furb.rotasegura.domain.entities.Role;
import br.com.furb.rotasegura.domain.enumerators.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Role findByRole(Roles role);

    Boolean existsByRole(Roles role);
}

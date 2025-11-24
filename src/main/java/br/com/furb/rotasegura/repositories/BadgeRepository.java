package br.com.furb.rotasegura.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.furb.rotasegura.domain.entities.Badge;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByPosition(Integer position);
}

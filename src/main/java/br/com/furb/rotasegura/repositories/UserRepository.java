package br.com.furb.rotasegura.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.furb.rotasegura.domain.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmailIgnoreCase(String email);
}

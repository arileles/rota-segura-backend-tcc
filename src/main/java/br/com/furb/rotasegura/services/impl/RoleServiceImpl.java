package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.domain.entities.Role;
import br.com.furb.rotasegura.repositories.UserRepository;
import br.com.furb.rotasegura.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;

    public RoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Set<Role>> findRolesByUserId(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles());
    }
}


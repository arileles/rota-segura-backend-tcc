package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.configurations.envs.MasterUserEnvironment;
import br.com.furb.rotasegura.domain.entities.Role;
import br.com.furb.rotasegura.domain.entities.User;
import br.com.furb.rotasegura.domain.enumerators.Roles;
import br.com.furb.rotasegura.repositories.RoleRepository;
import br.com.furb.rotasegura.repositories.UserRepository;
import br.com.furb.rotasegura.services.SchedulerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MasterUserEnvironment masterUserEnvironment;

    @Override
    @Transactional
    public void createRoles() {
        Roles.getRolesAsList().forEach(role -> {
            if (!roleRepository.existsByRole(role)) {
                roleRepository.save(new Role(UUID.randomUUID(), role));
            }
        });
    }

    @Override
    @Transactional
    public void createMasterUser() {
        if (!userRepository.existsByEmailIgnoreCase(masterUserEnvironment.getEmail())) {
            var masterUser = new User();
            masterUser.setId(UUID.randomUUID());
            masterUser.setName(masterUserEnvironment.getName());
            masterUser.setPassword(new BCryptPasswordEncoder().encode(masterUserEnvironment.getPassword()));
            masterUser.setIsActive(true);
            masterUser.setEmail(masterUserEnvironment.getEmail());
            masterUser.setRoles(Set.of(roleRepository.findByRole(Roles.ADMIN)));
            userRepository.save(masterUser);
        }
    }
}

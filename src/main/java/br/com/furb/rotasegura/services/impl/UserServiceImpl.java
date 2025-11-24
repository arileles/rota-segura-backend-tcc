package br.com.furb.rotasegura.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.furb.rotasegura.domain.entities.Role;
import br.com.furb.rotasegura.domain.entities.User;
import br.com.furb.rotasegura.domain.enumerators.Roles;
import br.com.furb.rotasegura.domain.records.RoleRecord;
import br.com.furb.rotasegura.domain.records.UserRecord;
import br.com.furb.rotasegura.exception.ResourceNotFoundException;
import br.com.furb.rotasegura.repositories.UserRepository;
import br.com.furb.rotasegura.services.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserRecord> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserServiceImpl::mapEntityToRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRecord findById(UUID id) {
        return userRepository.findById(id)
                .map(UserServiceImpl::mapEntityToRecord)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public UserRecord save(UserRecord ur) {
        // Ensure id is set if absent
        User user = mapRecordToEntity(ur);
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        return mapEntityToRecord(userRepository.save(user));
    }

    @Override
    public UserRecord update(UUID id, UserRecord data) {

        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }

        User user = existing.get();
        user.setName(data.name());
        user.setEmail(data.email());
        user.setUpdatedDate(LocalDateTime.now());
        return mapEntityToRecord(userRepository.save(user));
    }

    @Override
    public void delete(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.delete(user.get());
    }

    private static UserRecord mapEntityToRecord(User user) {
        return new UserRecord(user.getId(), user.getPassword(), user.getName(), user.getEmail(),
                mapRoleToRecord(user.getRoles()), user.getCreatedDate(), user.getBirthDate(), user.getPhone(), user.getLevel());
    }

    private static User mapRecordToEntity(UserRecord record) {
        // FIXME: Precisamos adicionar ou remover os demais campos que não estão no
        // UserRecord.
        return new User(record.id(), record.password(), record.name(), record.email(), true, LocalDateTime.now(),
                LocalDateTime.now(), record.phone(), record.birthDate() , record.level(), mapRoleToEntity(record.role()));
    }

    private static Set<RoleRecord> mapRoleToRecord(Set<Role> role) {
        if (role == null) {
            return Set.of();
        }
        return role.stream().map(UserServiceImpl::mapRoleToRecord).collect(Collectors.toSet());
    }

    private static RoleRecord mapRoleToRecord(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleRecord(role.getId(), role.getAuthority());
    }

    private static Set<Role> mapRoleToEntity(Set<RoleRecord> record) {
        if (record == null) {
            return Set.of();
        }
        return record.stream().map(UserServiceImpl::mapRoleToEntity).collect(Collectors.toSet());
    }

    private static Role mapRoleToEntity(RoleRecord record) {
        if (record == null) {
            return null;
        }
        return new Role(record.id(), Roles.getByName(record.roleName()));
    }
}

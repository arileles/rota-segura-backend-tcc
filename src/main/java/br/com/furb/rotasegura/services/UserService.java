package br.com.furb.rotasegura.services;

import br.com.furb.rotasegura.domain.records.UserRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    Page<UserRecord> findAll(Pageable pageable);

    UserRecord findById(UUID id);

    UserRecord save(UserRecord user);

    UserRecord update(UUID id, UserRecord data);

    void delete(UUID id);
}


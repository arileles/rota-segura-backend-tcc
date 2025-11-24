package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.domain.entities.Occurrence;
import br.com.furb.rotasegura.domain.entities.Role;
import br.com.furb.rotasegura.domain.entities.User;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceStatus;
import br.com.furb.rotasegura.domain.enumerators.Points;
import br.com.furb.rotasegura.domain.enumerators.Roles;
import br.com.furb.rotasegura.domain.records.*;
import br.com.furb.rotasegura.infra.exception.ServiceException;
import br.com.furb.rotasegura.repositories.OccurenceRepository;
import br.com.furb.rotasegura.repositories.UserRepository;
import br.com.furb.rotasegura.services.OccurrenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class OccurrenceServiceImpl implements OccurrenceService {

    @Autowired
    private OccurenceRepository occurenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporterCountRecord> findTopReporters() {
        var rows = occurenceRepository.findTopReportersNative();
        List<ReporterCountRecord> result = new ArrayList<>();
        if (rows == null) return result;
        for (Object[] row : rows) {
            if (row == null || row.length < 2) continue;
            UUID userId;
            try {
                if (row[0] instanceof UUID) userId = (UUID) row[0];
                else userId = UUID.fromString(row[0].toString());
            } catch (Exception ex) {
                continue; // skip invalid ids
            }
            long count;
            if (row[1] instanceof Number) count = ((Number) row[1]).longValue();
            else {
                try { count = Long.parseLong(row[1].toString()); } catch (Exception e) { count = 0; }
            }

            // Load full user entity and map to UserRecord
            var optUser = userRepository.findById(userId);
            if (optUser.isEmpty()) continue;
            User user = optUser.get();
            UserRecordCount ur = mapUserEntityToRecordCount(user);
            result.add(new ReporterCountRecord(ur, count));
        }
        return result;
    }

    private UserRecord mapUserEntityToRecord(User user) {
        if (user == null) return null;
        return new UserRecord(user.getId(), user.getPassword(), user.getName(), user.getEmail(),
                mapRoleEntitiesToRecord(user.getRoles()), user.getCreatedDate(), user.getBirthDate(), user.getPhone(), user.getLevel());
    }

    private UserRecordCount mapUserEntityToRecordCount(User user) {
        if (user == null) return null;
        return new UserRecordCount(user.getId(), user.getName(), user.getEmail(), user.getCreatedDate(), user.getLevel());
    }


        private Set<RoleRecord> mapRoleEntitiesToRecord(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(r -> new RoleRecord(r.getId(), r.getAuthority())).collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OccurrenceRecord> findAllOccurrences(OccurrenceStatus status, OccurrenceSeverity occurrenceSeverity) {

        // Se ambos nulos -> busca geral
        if (status == null && occurrenceSeverity == null) {
            return occurenceRepository.findAllByOrderByReportedAtDesc().stream()
                    .map(this::mapEntityToRecord)
                    .collect(Collectors.toList());
        }

        // Se apenas status informado
        if (status != null && occurrenceSeverity == null) {
            return occurenceRepository.findAllByStatusOrderByReportedAtDesc(status)
                    .stream()
                    .map(this::mapEntityToRecord)
                    .collect(Collectors.toList());
        }

        // Se apenas severity informado
        if (status == null && occurrenceSeverity != null) {
            return occurenceRepository.findAllBySeverityOrderByReportedAtDesc(occurrenceSeverity)
                    .stream()
                    .map(this::mapEntityToRecord)
                    .collect(Collectors.toList());
        }

        // Ambos informados
        return occurenceRepository.findAllByStatusAndSeverityOrderByReportedAtDesc(status, occurrenceSeverity)
                .stream()
                .map(this::mapEntityToRecord)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OccurrenceRecord findOccurrenceById(UUID id) {
        var occurrence = occurenceRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Não encontrado"));
        return mapEntityToRecord(occurrence);
    }

    @Override
    @Transactional
    public OccurrenceRecord save(OccurrenceRecord occurrenceRecord) {
        var occurrenceEntity = mapRecordToEntity(occurrenceRecord);
        occurrenceEntity.setId(UUID.randomUUID());
        var saved = occurenceRepository.save(occurrenceEntity);
        updateUserBadge(saved.getReporterUser());
        return mapEntityToRecord(saved);
    }

    @Override
    @Transactional
    public OccurrenceRecord update(UUID id, OccurrenceRecord data) {
        var currentEntity = occurenceRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Não encontrado"));

        currentEntity.setType(data.type());
        currentEntity.setSeverity(data.severity());
        currentEntity.setStatus(data.status());
        currentEntity.setDescription(data.description());
        currentEntity.setReportedAt(data.reportedAt());
        currentEntity.setResolvedAt(data.resolvedAt());
        currentEntity.setValidated(data.validated());
        currentEntity.setReporterUser(mapUserRecordToEntity(data.reporterUser()));

        var updated = occurenceRepository.save(currentEntity);
        return mapEntityToRecord(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!occurenceRepository.existsById(id)) {
            throw new ServiceException(HttpStatus.NOT_FOUND, "Não encontrado");
        }
        occurenceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OccurrenceRecord> findOccurrencesByUserId(UUID userId) {
        return occurenceRepository.findAllByReporterUserIdOrderByReportedAtDesc(userId).stream()
                .map(this::mapEntityToRecord).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OccurrenceRecord> searchOccurrences(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        String q = query.trim();
        return occurenceRepository
                .findAllByDescriptionContainingIgnoreCaseOrAddressContainingIgnoreCaseOrderByReportedAtDesc(q, q).stream()
                .map(this::mapEntityToRecord).toList();
    }

    // Novo método para atualizar apenas o status
    @Override
    @Transactional
    public OccurrenceRecord updateStatus(UUID id, OccurrenceStatus status) {
        var currentEntity = occurenceRepository.findById(id)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Não encontrado"));

        currentEntity.setStatus(status);
        if (status == OccurrenceStatus.RESOLVED) {
            currentEntity.setResolvedAt(LocalDateTime.now());
        } else {
            currentEntity.setResolvedAt(null);
        }

        var updated = occurenceRepository.save(currentEntity);
        return mapEntityToRecord(updated);
    }

    private OccurrenceRecord mapEntityToRecord(Occurrence occurrence) {
        return new OccurrenceRecord(
                occurrence.getId(),
                occurrence.getType(),
                occurrence.getSeverity(),
                occurrence.getStatus(),
                occurrence.getDescription(),
                occurrence.getReportedAt(),
                occurrence.getResolvedAt(),
                occurrence.getValidated(),
                occurrence.getReporterUser().getId().toString(),
                occurrence.getLatitude(),
                occurrence.getLongitude(),
                occurrence.getAddress(),
                occurrence.getAiSeverity(),
                occurrence.getAiType()
        );
    }

    private Occurrence mapRecordToEntity(OccurrenceRecord record) {
        if (record == null) return null;

        Occurrence occurrence = new Occurrence();
        occurrence.setId(record.id());
        occurrence.setType(record.type());
        occurrence.setSeverity(record.severity());
        occurrence.setStatus(record.status());
        occurrence.setDescription(record.description());
        occurrence.setReportedAt(record.reportedAt());
        occurrence.setResolvedAt(record.resolvedAt());
        occurrence.setValidated(record.validated());
        occurrence.setReporterUser(mapUserRecordToEntity(record.reporterUser()));
        occurrence.setLatitude(record.latitude());
        occurrence.setLongitude(record.longitude());
        occurrence.setAddress(record.address());
        occurrence.setAiSeverity(record.aiSeverity());
        occurrence.setAiType(record.aiType());
        return occurrence;
    }

    private User mapUserRecordToEntity(String userRecord) {
        return userRepository.findById(UUID.fromString(userRecord))
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "User not found: " + userRecord));
    }


    // Mapeamento RoleEntity -> RoleRecord
    private RoleRecord mapRoleEntityToRecord(Role role) {
        if (role == null) return null;
        return new RoleRecord(role.getId(), role.getRole().name());
    }

    // Mapeamento RoleRecord -> RoleEntity
    private Role mapRoleRecordToEntity(RoleRecord roleRecord) {
        if (roleRecord == null) return null;
        return new Role(roleRecord.id(), Roles.valueOf(roleRecord.roleName()));
    }

    // Retorna a quantidade de ocorrências de um usuário específico
    private int getTotalOccurences(UUID userId) {
        if (userId == null) return 0;
        return (int) occurenceRepository.countAllByReporterUserId(userId);
    }

    public void updateUserBadge(User user) {
        int total = getTotalOccurences(user.getId());
         for (Points p : Points.getSortedLevels()) {
             if (total >= p.getCount()) {
                 user.setLevel(p.getLevel());
                 break;
             }
         }
     }
 }

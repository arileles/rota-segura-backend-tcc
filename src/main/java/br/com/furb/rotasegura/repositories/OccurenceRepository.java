package br.com.furb.rotasegura.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.furb.rotasegura.domain.entities.Occurrence;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceStatus;

    @Repository
    public interface OccurenceRepository extends JpaRepository<Occurrence, UUID> {

    List<Occurrence> findAllByReporterUserIdOrderByReportedAtDesc(UUID userId);

    // Busca por status
    List<Occurrence> findAllByStatusOrderByReportedAtDesc(OccurrenceStatus status);

    // Busca por severidade
    List<Occurrence> findAllBySeverityOrderByReportedAtDesc(OccurrenceSeverity severity);

    // Busca por status e severidade juntos
    List<Occurrence> findAllByStatusAndSeverityOrderByReportedAtDesc(OccurrenceStatus status, OccurrenceSeverity severity);

    // Busca por texto em description ou address (LIKE, case-insensitive)
    List<Occurrence> findAllByDescriptionContainingIgnoreCaseOrAddressContainingIgnoreCaseOrderByReportedAtDesc(String description, String address);

    // Retorna lista de [reporter_user_id, occurrences_count] ordenada desc (limit controlado pelo parâmetro)
    @Query(value = "select reporter_user_id, count(*) as occurrences_count from public.occurrence group by reporter_user_id order by occurrences_count desc", nativeQuery = true)
    java.util.List<Object[]> findTopReportersNative();

    // corrected method name: Spring Data expects 'findAllByOrderBy<Property>Desc()'
    List<Occurrence> findAllByOrderByReportedAtDesc();

    // Conta quantas ocorrências um usuário reportou
    long countAllByReporterUserId(UUID userId);
}

package br.com.furb.rotasegura.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.furb.rotasegura.domain.entities.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findByOccurrenceId(UUID occurrenceId);
}

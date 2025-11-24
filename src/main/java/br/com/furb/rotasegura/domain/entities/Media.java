package br.com.furb.rotasegura.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media", schema = "public")
@EntityListeners(AuditingEntityListener.class)
public class Media {

    @Id
    @Column
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occurrence_id")
    private Occurrence occurrence;

    @Column(name = "media_type")
    private String mediaType;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_type")
    private OccurrenceType aiType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_severity")
    private OccurrenceSeverity aiSeverity;
}

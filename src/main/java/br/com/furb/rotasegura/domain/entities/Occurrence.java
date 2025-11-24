package br.com.furb.rotasegura.domain.entities;

import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceStatus;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "occurrence", schema = "public")
@EntityListeners(AuditingEntityListener.class)
public class Occurrence {

    @Id
    @Column
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column
    private OccurrenceType type;

    @Enumerated(EnumType.STRING)
    @Column
    private OccurrenceSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_type")
    private OccurrenceType aiType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_severity")
    private OccurrenceSeverity aiSeverity;
    
    @Enumerated(EnumType.STRING)
    @Column
    private OccurrenceStatus status;

    @Column
    private String description;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String address;

    @CreatedDate
    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column
    private Boolean validated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id")
    private User reporterUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_user")
    private User resolvedByUser;
}

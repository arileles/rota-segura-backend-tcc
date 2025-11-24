package br.com.furb.rotasegura.domain.entities;

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
@Table(name = "badge", schema = "public")
@EntityListeners(AuditingEntityListener.class)
public class Badge {

    @Id
    @Column
    private UUID id;

    @Column
    private String name;

    @Column
    private Integer minPoints;

    @Column
    private Integer position;

    @Column
    private Boolean isActive;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

}

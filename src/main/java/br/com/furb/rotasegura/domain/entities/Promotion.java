package br.com.furb.rotasegura.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotion", schema = "public")
@EntityListeners(AuditingEntityListener.class)
public class Promotion {

    @Id
    @Column
    private UUID id;

    @Column
    private String name;

    @Column
    private String couponCode;

    @Column(name = "required_points")
    private Integer requiredPoints;

}

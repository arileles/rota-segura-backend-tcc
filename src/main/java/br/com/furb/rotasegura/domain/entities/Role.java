package br.com.furb.rotasegura.domain.entities;

import br.com.furb.rotasegura.domain.enumerators.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role", schema = "public")
public class Role implements GrantedAuthority {

    @Id
    @Column
    private UUID id;

    @Column
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}

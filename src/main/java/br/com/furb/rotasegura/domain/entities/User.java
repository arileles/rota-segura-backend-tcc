package br.com.furb.rotasegura.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "public")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @Column()
    private UUID id;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private Boolean isActive;

    @Column
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "level")
    private Integer level;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name =  "id_role")
    )
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {return email;}

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}

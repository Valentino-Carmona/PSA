package com.psa.proyecto_api.model;

import com.psa.proyecto_api.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false, length = 100)
    private String lastName;

    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Retorna true por defecto. Modificar e implementar lógica en caso de
        // requerir cuentas que expiren (ej: suscripciones o membresías).
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Retorna true por defecto. Modificar e implementar lógica en caso de
        // requerir bloqueo de cuentas (ej: tras varios intentos fallidos de login).
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Retorna true por defecto. Modificar e implementar lógica en caso de
        // requerir obligar a los usuarios a cambiar su contraseña cada N días.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Retorna true por defecto. Modificar e implementar lógica si se desea
        // utilizar "soft-delete" (desactivar usuarios en lugar de eliminarlos de la DB).
        return true;
    }
}

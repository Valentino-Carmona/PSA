package com.psa.proyecto_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

/**
 * Entidad que representa un tag reutilizable en el sistema (Catálogo Maestro de Tags).
 */
@Entity
@Table(name = "tags", indexes = {
    @Index(name = "idx_tags_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del tag es obligatorio")
    @Size(max = 50, message = "El nombre del tag no puede exceder los 50 caracteres")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Tag(String name) {
        this.name = name.toLowerCase().trim();
    }

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (this.name != null) {
            this.name = this.name.toLowerCase().trim();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;
        return Objects.equals(name, tag.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}

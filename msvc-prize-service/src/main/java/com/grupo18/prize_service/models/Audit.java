package com.grupo18.prize_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Audit {

    // Guarda el momento exacto en el que se creó el registro del premio o presupuesto
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Guarda el momento en el que se actualiza (ej: cuando el premio pasa de "PENDIENTE" a "ENTREGADO")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();}
}
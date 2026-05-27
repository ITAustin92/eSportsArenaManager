package com.grupo18.ranking_service.models;

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

    // Esta columna guarda cuándo se creó la entrada en el ranking por primera vez
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Esta columna guarda cuándo fue la última vez que le sumamos puntos al equipo
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();}
}
package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.petshop.model.enums.EstadoCita;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas_servicio")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "mascota.cliente", "usuario"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EstadoCita estado = EstadoCita.AGENDADA;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}

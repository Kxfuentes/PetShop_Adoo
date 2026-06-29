package com.petshop.dto;

import com.petshop.model.enums.EstadoCita;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CitaServicioResponse {

    private Long id;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private String observacion;
    private Long clienteId;
    private String clienteNombre;
    private Long mascotaId;
    private String mascotaNombre;
    private Long servicioId;
    private String servicioNombre;
    private Integer duracionMinutos;
    private BigDecimal precio;
}

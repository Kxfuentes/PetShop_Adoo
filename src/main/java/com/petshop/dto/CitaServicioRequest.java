package com.petshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaServicioRequest {

    @NotNull
    private Long mascotaId;

    @NotNull
    private Long servicioId;

    @NotNull
    private LocalDateTime fechaHora;

    private String observacion;
}

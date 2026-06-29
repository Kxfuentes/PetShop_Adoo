package com.petshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioRequest {

    @NotBlank
    private String nombre;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;

    @NotNull
    @Min(1)
    private Integer duracionMinutos;

    private String descripcion;
}

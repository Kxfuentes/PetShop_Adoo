package com.petshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MascotaRequest {

    @NotBlank
    private String nombre;

    private String especie;
    private String raza;
    private Integer edad;
    private BigDecimal peso;
    private String observaciones;

    @NotNull
    private Long clienteId;
}

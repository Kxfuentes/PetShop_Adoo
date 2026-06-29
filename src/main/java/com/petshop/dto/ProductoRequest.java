package com.petshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotNull
    @Min(0)
    private Integer stockMinimo;

    private Long categoriaId;
}

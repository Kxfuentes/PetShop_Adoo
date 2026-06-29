package com.petshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VentaRequest {

    @NotNull
    private Long clienteId;

    @NotEmpty
    @Valid
    private List<DetalleVentaRequest> detalles;
}

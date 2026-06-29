package com.petshop.dto;

import com.petshop.model.enums.TipoMovimientoInventario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovimientoInventarioRequest {

    @NotNull
    private Long productoId;

    @NotNull
    private TipoMovimientoInventario tipoMovimiento;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private String motivo;
}

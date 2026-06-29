package com.petshop.dto;

import com.petshop.model.enums.EstadoVenta;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VentaResponse {

    private Long id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private EstadoVenta estado;
    private Long clienteId;
    private String clienteNombre;
    private List<DetalleVentaResponse> detalles;
}

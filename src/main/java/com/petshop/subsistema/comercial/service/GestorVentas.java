package com.petshop.subsistema.comercial.service;

import com.petshop.dto.DetalleVentaRequest;
import com.petshop.dto.DetalleVentaResponse;
import com.petshop.dto.MovimientoInventarioRequest;
import com.petshop.dto.VentaRequest;
import com.petshop.dto.VentaResponse;
import com.petshop.exception.OperacionInvalidaException;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.*;
import com.petshop.model.enums.EstadoVenta;
import com.petshop.subsistema.comercial.repository.VentaRepository;
import com.petshop.subsistema.servicios.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * GestorVentas - Controlador de lógica de negocio del subsistema comercial.
 * Orquesta registro, validación, pago y anulación de ventas.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GestorVentas {

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public VentaResponse registrarVenta(VentaRequest request, Usuario cajero) {
        Cliente cliente = clienteService.buscar(request.getClienteId());

        Venta venta = Venta.builder()
                .cliente(cliente)
                .usuario(cajero)
                .estado(EstadoVenta.INICIADA)
                .build();

        for (DetalleVentaRequest detalleReq : request.getDetalles()) {
            Producto producto = productoService.buscar(detalleReq.getProductoId());
            inventarioService.validarStock(producto, detalleReq.getCantidad());

            DetalleVenta detalle = DetalleVenta.builder()
                    .producto(producto)
                    .cantidad(detalleReq.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
            detalle.calcularSubtotal();
            venta.agregarDetalle(detalle);
        }

        venta.calcularTotal();
        venta.setEstado(EstadoVenta.VALIDADA);

        Venta ventaGuardada = ventaRepository.save(venta);
        confirmarVenta(ventaGuardada.getId());

        return toResponse(ventaRepository.findById(ventaGuardada.getId()).orElseThrow());
    }

    public VentaResponse confirmarVenta(Long id) {
        Venta venta = buscar(id);

        if (venta.getEstado() == EstadoVenta.PAGADA) {
            throw new OperacionInvalidaException("La venta ya está pagada");
        }
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new OperacionInvalidaException("No se puede confirmar una venta anulada");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            inventarioService.descontarStock(
                    detalle.getProducto(),
                    detalle.getCantidad(),
                    "Venta #" + venta.getId());
        }

        venta.setEstado(EstadoVenta.PAGADA);
        venta.calcularTotal();
        validarTotalDetalles(venta);

        return toResponse(ventaRepository.save(venta));
    }

    public VentaResponse anularVenta(Long id, Usuario usuario) {
        Venta venta = buscar(id);

        if (venta.getEstado() == EstadoVenta.PAGADA
                && usuario.getRol() != com.petshop.model.enums.RolUsuario.ADMIN) {
            throw new OperacionInvalidaException(
                    "Solo un administrador puede anular una venta pagada");
        }

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new OperacionInvalidaException("La venta ya está anulada");
        }

        if (venta.getEstado() == EstadoVenta.PAGADA) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                inventarioService.registrarMovimiento(MovimientoInventarioRequest.builder()
                        .productoId(detalle.getProducto().getId())
                        .tipoMovimiento(com.petshop.model.enums.TipoMovimientoInventario.ENTRADA)
                        .cantidad(detalle.getCantidad())
                        .motivo("Anulación venta #" + venta.getId())
                        .build());
            }
        }

        venta.setEstado(EstadoVenta.ANULADA);
        return toResponse(ventaRepository.save(venta));
    }

    @Transactional(readOnly = true)
    public Venta buscar(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return ventaRepository.findAll().stream().map(this::toResponse).toList();
    }

    private void validarTotalDetalles(Venta venta) {
        BigDecimal sumaDetalles = venta.getDetalles().stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (venta.getTotal().compareTo(sumaDetalles) != 0) {
            throw new OperacionInvalidaException(
                    "El total de la venta no coincide con la suma de los subtotales");
        }
    }

    @Transactional(readOnly = true)
    public VentaResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    public VentaResponse toResponse(Venta venta) {
        List<DetalleVentaResponse> detalles = venta.getDetalles().stream()
                .map(d -> DetalleVentaResponse.builder()
                        .productoId(d.getProducto().getId())
                        .productoNombre(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .toList();

        return VentaResponse.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .total(venta.getTotal())
                .estado(venta.getEstado())
                .clienteId(venta.getCliente().getId())
                .clienteNombre(venta.getCliente().getNombre())
                .detalles(detalles)
                .build();
    }
}

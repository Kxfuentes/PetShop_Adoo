package com.petshop.subsistema.comercial.service;

import com.petshop.dto.MovimientoInventarioRequest;
import com.petshop.exception.StockInsuficienteException;
import com.petshop.model.Inventario;
import com.petshop.model.Producto;
import com.petshop.model.enums.TipoMovimientoInventario;
import com.petshop.subsistema.comercial.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoService productoService;

    public Inventario registrarMovimiento(MovimientoInventarioRequest request) {
        Producto producto = productoService.buscar(request.getProductoId());
        boolean esEntrada = request.getTipoMovimiento() == TipoMovimientoInventario.ENTRADA;

        if (!esEntrada && !producto.verificarDisponibilidad(request.getCantidad())) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto '" + producto.getNombre()
                            + "'. Disponible: " + producto.getStock()
                            + ", solicitado: " + request.getCantidad());
        }

        producto.actualizarStock(request.getCantidad(), esEntrada);

        Inventario movimiento = Inventario.builder()
                .producto(producto)
                .tipoMovimiento(request.getTipoMovimiento())
                .cantidad(request.getCantidad())
                .motivo(request.getMotivo())
                .build();

        return inventarioRepository.save(movimiento);
    }

    public void validarStock(Producto producto, int cantidad) {
        if (!producto.verificarDisponibilidad(cantidad)) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para el producto '" + producto.getNombre()
                            + "'. Disponible: " + producto.getStock()
                            + ", solicitado: " + cantidad);
        }
    }

    public void descontarStock(Producto producto, int cantidad, String motivo) {
        validarStock(producto, cantidad);
        producto.actualizarStock(cantidad, false);
        inventarioRepository.save(Inventario.builder()
                .producto(producto)
                .tipoMovimiento(TipoMovimientoInventario.SALIDA)
                .cantidad(cantidad)
                .motivo(motivo)
                .build());
    }

    @Transactional(readOnly = true)
    public List<Inventario> listarPorProducto(Long productoId) {
        productoService.buscar(productoId);
        return inventarioRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    @Transactional(readOnly = true)
    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> generarAlertasStockMinimo() {
        return productoService.listarConAlertaStock();
    }
}

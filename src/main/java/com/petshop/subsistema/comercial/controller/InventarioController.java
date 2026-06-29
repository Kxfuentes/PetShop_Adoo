package com.petshop.subsistema.comercial.controller;

import com.petshop.dto.MovimientoInventarioRequest;
import com.petshop.model.Inventario;
import com.petshop.model.Producto;
import com.petshop.subsistema.comercial.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comercial/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping("/movimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public Inventario registrarMovimiento(@Valid @RequestBody MovimientoInventarioRequest request) {
        return inventarioService.registrarMovimiento(request);
    }

    @GetMapping("/movimientos")
    public List<Inventario> listarMovimientos() {
        return inventarioService.listar();
    }

    @GetMapping("/movimientos/producto/{productoId}")
    public List<Inventario> listarPorProducto(@PathVariable Long productoId) {
        return inventarioService.listarPorProducto(productoId);
    }

    @GetMapping("/alertas")
    public List<Producto> alertasStockMinimo() {
        return inventarioService.generarAlertasStockMinimo();
    }
}

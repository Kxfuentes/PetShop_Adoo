package com.petshop.subsistema.comercial.controller;

import com.petshop.dto.ProductoRequest;
import com.petshop.model.Producto;
import com.petshop.subsistema.comercial.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comercial/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @PutMapping("/{id}")
    public Producto modificar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.modificar(id, request);
    }

    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return productoService.buscar(id);
    }

    @GetMapping("/alertas-stock")
    public List<Producto> alertasStock() {
        return productoService.listarConAlertaStock();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}

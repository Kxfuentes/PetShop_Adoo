package com.petshop.subsistema.servicios.controller;

import com.petshop.dto.ServicioRequest;
import com.petshop.model.Servicio;
import com.petshop.subsistema.servicios.service.ServicioOfrecidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioOfrecidoService servicioOfrecidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Servicio crear(@Valid @RequestBody ServicioRequest request) {
        return servicioOfrecidoService.crear(request);
    }

    @PutMapping("/{id}")
    public Servicio modificar(@PathVariable Long id, @Valid @RequestBody ServicioRequest request) {
        return servicioOfrecidoService.modificarPrecio(id, request);
    }

    @GetMapping
    public List<Servicio> listar() {
        return servicioOfrecidoService.listar();
    }

    @GetMapping("/{id}")
    public Servicio obtener(@PathVariable Long id) {
        return servicioOfrecidoService.buscar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicioOfrecidoService.eliminar(id);
    }
}

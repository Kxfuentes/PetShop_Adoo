package com.petshop.subsistema.servicios.controller;

import com.petshop.dto.MascotaRequest;
import com.petshop.model.Mascota;
import com.petshop.subsistema.servicios.service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mascota registrar(@Valid @RequestBody MascotaRequest request) {
        return mascotaService.registrar(request);
    }

    @PutMapping("/{id}")
    public Mascota actualizar(@PathVariable Long id, @Valid @RequestBody MascotaRequest request) {
        return mascotaService.actualizar(id, request);
    }

    @GetMapping
    public List<Mascota> listar() {
        return mascotaService.listar();
    }

    @GetMapping("/{id}")
    public Mascota obtener(@PathVariable Long id) {
        return mascotaService.buscar(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Mascota> listarPorCliente(@PathVariable Long clienteId) {
        return mascotaService.listarPorCliente(clienteId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
    }
}

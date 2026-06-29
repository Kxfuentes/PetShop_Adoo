package com.petshop.subsistema.comercial.controller;

import com.petshop.dto.VentaRequest;
import com.petshop.dto.VentaResponse;
import com.petshop.model.Usuario;
import com.petshop.service.UsuarioService;
import com.petshop.subsistema.comercial.service.GestorVentas;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comercial/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final GestorVentas gestorVentas;
    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrar(@Valid @RequestBody VentaRequest request, Authentication auth) {
        Usuario cajero = usuarioService.buscarEntidadPorCorreo(auth.getName());
        return gestorVentas.registrarVenta(request, cajero);
    }

    @PostMapping("/{id}/confirmar")
    public VentaResponse confirmar(@PathVariable Long id) {
        return gestorVentas.confirmarVenta(id);
    }

    @PostMapping("/{id}/anular")
    public VentaResponse anular(@PathVariable Long id, Authentication auth) {
        Usuario usuario = usuarioService.buscarEntidadPorCorreo(auth.getName());
        return gestorVentas.anularVenta(id, usuario);
    }

    @GetMapping
    public List<VentaResponse> listar() {
        return gestorVentas.listar();
    }

    @GetMapping("/{id}")
    public VentaResponse obtener(@PathVariable Long id) {
        return gestorVentas.obtenerPorId(id);
    }
}

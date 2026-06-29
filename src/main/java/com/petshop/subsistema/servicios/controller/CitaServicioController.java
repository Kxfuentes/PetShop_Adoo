package com.petshop.subsistema.servicios.controller;

import com.petshop.dto.CitaServicioRequest;
import com.petshop.dto.CitaServicioResponse;
import com.petshop.dto.ReprogramarCitaRequest;
import com.petshop.model.CitaServicio;
import com.petshop.model.Usuario;
import com.petshop.service.UsuarioService;
import com.petshop.subsistema.servicios.service.GestorCitas;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios/citas")
@RequiredArgsConstructor
public class CitaServicioController {

    private final GestorCitas gestorCitas;
    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaServicioResponse agendar(@Valid @RequestBody CitaServicioRequest request, Authentication auth) {
        Usuario recepcionista = usuarioService.buscarEntidadPorCorreo(auth.getName());
        return gestorCitas.agendarResponse(request, recepcionista);
    }

    @PostMapping("/{id}/confirmar")
    public CitaServicioResponse confirmar(@PathVariable Long id) {
        return gestorCitas.confirmarResponse(id);
    }

    @PostMapping("/{id}/cancelar")
    public CitaServicioResponse cancelar(@PathVariable Long id) {
        return gestorCitas.cancelarResponse(id);
    }

    @PostMapping("/{id}/iniciar-atencion")
    public CitaServicioResponse iniciarAtencion(@PathVariable Long id) {
        return gestorCitas.iniciarAtencionResponse(id);
    }

    @PostMapping("/{id}/registrar-atencion")
    public CitaServicioResponse registrarAtencion(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return gestorCitas.registrarAtencionResponse(id, body.get("observacion"));
    }

    @PostMapping("/{id}/reprogramar")
    public CitaServicioResponse reprogramar(@PathVariable Long id, @Valid @RequestBody ReprogramarCitaRequest request) {
        return gestorCitas.reprogramarResponse(id, request);
    }

    @GetMapping("/disponibilidad")
    public Map<String, Boolean> consultarDisponibilidad(
            @RequestParam Long mascotaId,
            @RequestParam Long servicioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        boolean disponible = gestorCitas.consultarDisponibilidad(mascotaId, servicioId, fechaHora);
        return Map.of("disponible", disponible);
    }

    @GetMapping
    public List<CitaServicioResponse> listar() {
        return gestorCitas.listarResponse();
    }

    @GetMapping("/{id}")
    public CitaServicioResponse obtener(@PathVariable Long id) {
        return gestorCitas.obtenerResponse(id);
    }

    @GetMapping("/mascota/{mascotaId}")
    public List<CitaServicioResponse> listarPorMascota(@PathVariable Long mascotaId) {
        return gestorCitas.listarPorMascota(mascotaId).stream()
                .map(gestorCitas::toResponse)
                .toList();
    }
}

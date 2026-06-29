package com.petshop.subsistema.servicios.service;

import com.petshop.dto.CitaServicioRequest;
import com.petshop.dto.CitaServicioResponse;
import com.petshop.dto.ReprogramarCitaRequest;
import com.petshop.exception.OperacionInvalidaException;
import com.petshop.exception.RecursoNoEncontradoException;
import com.petshop.model.CitaServicio;
import com.petshop.model.Servicio;
import com.petshop.model.Usuario;
import com.petshop.model.enums.EstadoCita;
import com.petshop.subsistema.servicios.repository.CitaServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * GestorCitas - Controlador de lógica de negocio del subsistema de servicios.
 * Orquesta agendamiento, confirmación y cancelación de citas.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GestorCitas {

    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(17, 0);

    private final CitaServicioRepository citaServicioRepository;
    private final MascotaService mascotaService;
    private final ServicioOfrecidoService servicioOfrecidoService;

    public CitaServicio agendar(CitaServicioRequest request, Usuario recepcionista) {
        var mascota = mascotaService.buscar(request.getMascotaId());
        Servicio servicio = servicioOfrecidoService.buscar(request.getServicioId());

        validarDisponibilidad(mascota.getId(), servicio, request.getFechaHora());

        CitaServicio cita = CitaServicio.builder()
                .mascota(mascota)
                .servicio(servicio)
                .fechaHora(request.getFechaHora())
                .observacion(request.getObservacion())
                .estado(EstadoCita.AGENDADA)
                .usuario(recepcionista)
                .build();

        return citaServicioRepository.save(cita);
    }

    public CitaServicioResponse agendarResponse(CitaServicioRequest request, Usuario recepcionista) {
        return toResponse(agendar(request, recepcionista));
    }

    public CitaServicio confirmar(Long id) {
        CitaServicio cita = buscar(id);
        if (cita.getEstado() != EstadoCita.AGENDADA && cita.getEstado() != EstadoCita.REPROGRAMADA) {
            throw new OperacionInvalidaException("Solo se pueden confirmar citas pendientes o reprogramadas");
        }
        cita.setEstado(EstadoCita.CONFIRMADA);
        return citaServicioRepository.save(cita);
    }

    public CitaServicio cancelar(Long id) {
        CitaServicio cita = buscar(id);
        if (cita.getEstado() == EstadoCita.FINALIZADA) {
            throw new OperacionInvalidaException("No se puede cancelar una cita finalizada");
        }
        cita.setEstado(EstadoCita.CANCELADA);
        return citaServicioRepository.save(cita);
    }

    public CitaServicio iniciarAtencion(Long id) {
        CitaServicio cita = buscar(id);
        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new OperacionInvalidaException("La cita debe estar confirmada para iniciar atención");
        }
        cita.setEstado(EstadoCita.EN_ATENCION);
        return citaServicioRepository.save(cita);
    }

    public CitaServicio registrarAtencion(Long id, String observacion) {
        CitaServicio cita = buscar(id);
        if (cita.getEstado() != EstadoCita.EN_ATENCION) {
            throw new OperacionInvalidaException("La cita debe estar EN_ATENCION para registrar la atención");
        }
        cita.setObservacion(observacion);
        cita.setEstado(EstadoCita.FINALIZADA);
        return citaServicioRepository.save(cita);
    }

    public CitaServicio reprogramar(Long id, ReprogramarCitaRequest request) {
        CitaServicio cita = buscar(id);
        if (cita.getEstado() == EstadoCita.FINALIZADA || cita.getEstado() == EstadoCita.CANCELADA) {
            throw new OperacionInvalidaException("No se puede reprogramar una cita finalizada o cancelada");
        }

        if (cita.getFechaHora().toLocalDate().equals(request.getFechaHora().toLocalDate())) {
            throw new OperacionInvalidaException("La nueva fecha debe ser diferente a la fecha actual de la cita");
        }

        Servicio servicio = cita.getServicio();
        Long mascotaId = cita.getMascota().getId();
        validarDisponibilidadParaCita(cita.getId(), mascotaId, servicio, request.getFechaHora());
        cita.setFechaHora(request.getFechaHora());
        cita.setEstado(EstadoCita.REPROGRAMADA);
        return citaServicioRepository.save(cita);
    }

    public CitaServicioResponse confirmarResponse(Long id) {
        return toResponse(confirmar(id));
    }

    public CitaServicioResponse cancelarResponse(Long id) {
        return toResponse(cancelar(id));
    }

    public CitaServicioResponse iniciarAtencionResponse(Long id) {
        return toResponse(iniciarAtencion(id));
    }

    public CitaServicioResponse registrarAtencionResponse(Long id, String observacion) {
        return toResponse(registrarAtencion(id, observacion));
    }

    public CitaServicioResponse reprogramarResponse(Long id, ReprogramarCitaRequest request) {
        return toResponse(reprogramar(id, request));
    }

    @Transactional(readOnly = true)
    public CitaServicio buscar(Long id) {
        return citaServicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<CitaServicio> listarPorMascota(Long mascotaId) {
        mascotaService.buscar(mascotaId);
        return citaServicioRepository.findByMascotaId(mascotaId);
    }

    @Transactional(readOnly = true)
    public List<CitaServicio> listar() {
        return citaServicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CitaServicioResponse> listarResponse() {
        return citaServicioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CitaServicioResponse obtenerResponse(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public boolean consultarDisponibilidad(Long mascotaId, Long servicioId, LocalDateTime fechaHora) {
        Servicio servicio = servicioOfrecidoService.buscar(servicioId);
        return estaEnHorarioLaboral(fechaHora, servicio) && !haySolapamiento(mascotaId, servicio, fechaHora);
    }

    private void validarDisponibilidad(Long mascotaId, Servicio servicio, LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new OperacionInvalidaException("No se puede agendar una cita en el pasado");
        }
        if (!estaEnHorarioLaboral(fechaHora, servicio)) {
            throw new OperacionInvalidaException("Las citas solo pueden agendarse de lunes a sábado entre 08:00 y 17:00, en bloques de 00 o 30 minutos");
        }
        if (haySolapamiento(mascotaId, servicio, fechaHora)) {
            throw new OperacionInvalidaException(
                    "La mascota ya tiene una cita programada en el horario seleccionado");
        }
    }

    private void validarDisponibilidadParaCita(Long citaId, Long mascotaId, Servicio servicio, LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new OperacionInvalidaException("No se puede agendar una cita en el pasado");
        }
        if (!estaEnHorarioLaboral(fechaHora, servicio)) {
            throw new OperacionInvalidaException("Las citas solo pueden agendarse de lunes a sábado entre 08:00 y 17:00, en bloques de 00 o 30 minutos");
        }
        if (haySolapamiento(mascotaId, servicio, fechaHora, citaId)) {
            throw new OperacionInvalidaException(
                    "La mascota ya tiene una cita programada en el horario seleccionado");
        }
    }

    private boolean estaEnHorarioLaboral(LocalDateTime inicio, Servicio servicio) {
        if (inicio.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        if (inicio.getMinute() != 0 && inicio.getMinute() != 30) {
            return false;
        }
        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = inicio.plusMinutes(servicio.getDuracionMinutos()).toLocalTime();
        return !horaInicio.isBefore(HORA_APERTURA) && !horaFin.isAfter(HORA_CIERRE);
    }

    private boolean haySolapamiento(Long mascotaId, Servicio servicio, LocalDateTime inicio) {
        return haySolapamiento(mascotaId, servicio, inicio, null);
    }

    private boolean haySolapamiento(Long mascotaId, Servicio servicio, LocalDateTime inicio, Long citaIgnoradaId) {
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());
        List<CitaServicio> citasActivas = citaServicioRepository
                .findByMascotaIdAndEstadoNot(mascotaId, EstadoCita.CANCELADA);

        for (CitaServicio cita : citasActivas) {
            if (citaIgnoradaId != null && cita.getId().equals(citaIgnoradaId)) {
                continue;
            }
            LocalDateTime citaInicio = cita.getFechaHora();
            LocalDateTime citaFin = citaInicio.plusMinutes(cita.getServicio().getDuracionMinutos());
            if (inicio.isBefore(citaFin) && fin.isAfter(citaInicio)) {
                return true;
            }
        }
        return false;
    }

    public CitaServicioResponse toResponse(CitaServicio cita) {
        return CitaServicioResponse.builder()
                .id(cita.getId())
                .fechaHora(cita.getFechaHora())
                .estado(cita.getEstado())
                .observacion(cita.getObservacion())
                .clienteId(cita.getMascota().getCliente().getId())
                .clienteNombre(cita.getMascota().getCliente().getNombre())
                .mascotaId(cita.getMascota().getId())
                .mascotaNombre(cita.getMascota().getNombre())
                .servicioId(cita.getServicio().getId())
                .servicioNombre(cita.getServicio().getNombre())
                .duracionMinutos(cita.getServicio().getDuracionMinutos())
                .precio(cita.getServicio().getPrecio())
                .build();
    }
}

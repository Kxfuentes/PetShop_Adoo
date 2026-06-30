package com.petshop.subsistema.servicios.repository;

import com.petshop.model.CitaServicio;
import com.petshop.model.enums.EstadoCita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaServicioRepository extends JpaRepository<CitaServicio, Long> {

    @Override
    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "servicio"})
    List<CitaServicio> findAll();

    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "servicio"})
    List<CitaServicio> findByMascotaId(Long mascotaId);

    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "servicio"})
    List<CitaServicio> findByMascotaIdAndEstadoNot(Long mascotaId, EstadoCita estado);

    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "servicio"})
    List<CitaServicio> findByEstado(EstadoCita estado);

    boolean existsByMascotaId(Long mascotaId);

    boolean existsByMascotaClienteId(Long clienteId);

    boolean existsByServicioId(Long servicioId);
}

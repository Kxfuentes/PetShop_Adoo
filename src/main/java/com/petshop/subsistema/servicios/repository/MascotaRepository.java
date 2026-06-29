package com.petshop.subsistema.servicios.repository;

import com.petshop.model.Mascota;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    @Override
    @EntityGraph(attributePaths = "cliente")
    List<Mascota> findAll();

    List<Mascota> findByClienteId(Long clienteId);
}

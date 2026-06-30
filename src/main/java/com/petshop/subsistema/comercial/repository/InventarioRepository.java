package com.petshop.subsistema.comercial.repository;

import com.petshop.model.Inventario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    @Override
    @EntityGraph(attributePaths = "producto")
    List<Inventario> findAll();

    @EntityGraph(attributePaths = "producto")
    List<Inventario> findByProductoIdOrderByFechaDesc(Long productoId);

    boolean existsByProductoId(Long productoId);
}

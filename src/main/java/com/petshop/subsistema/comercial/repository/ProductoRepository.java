package com.petshop.subsistema.comercial.repository;

import com.petshop.model.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Override
    @EntityGraph(attributePaths = "categoria")
    List<Producto> findAll();

    List<Producto> findByStockLessThanEqual(Integer stock);
}

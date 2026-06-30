package com.petshop.subsistema.comercial.repository;

import com.petshop.model.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Override
    @EntityGraph(attributePaths = "categoria")
    List<Producto> findAll();

    List<Producto> findByStockLessThanEqual(Integer stock);

    boolean existsByCategoriaId(Long categoriaId);

    @Query("select count(d) > 0 from DetalleVenta d where d.producto.id = :productoId")
    boolean existsInVentasByProductoId(@Param("productoId") Long productoId);
}

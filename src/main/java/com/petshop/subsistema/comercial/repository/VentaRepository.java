package com.petshop.subsistema.comercial.repository;

import com.petshop.model.Venta;
import com.petshop.model.enums.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEstado(EstadoVenta estado);

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
